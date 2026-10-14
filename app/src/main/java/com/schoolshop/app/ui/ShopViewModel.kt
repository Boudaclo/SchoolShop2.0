package com.schoolshop.app.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.schoolshop.app.data.CartItem
import com.schoolshop.app.data.Category
import com.schoolshop.app.data.DeliveryMode
import com.schoolshop.app.data.PaymentMode
import com.schoolshop.app.data.Product
import com.schoolshop.app.data.SampleData
import com.schoolshop.app.data.local.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ShopViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ShopDatabase.get(app).dao()

    // Données lues depuis Room (mises à jour automatiquement)
    var categories by mutableStateOf(listOf<Category>())
        private set
    var products by mutableStateOf(listOf<Product>())
        private set
    var cart by mutableStateOf(listOf<CartItem>())
        private set
    var favorites by mutableStateOf(setOf<Int>())
        private set
    var orders by mutableStateOf(listOf<OrderWithItems>())
        private set
    var addresses by mutableStateOf(listOf<AddressEntity>())
        private set
    var payments by mutableStateOf(listOf<PaymentMethodEntity>())
        private set
    var profileName by mutableStateOf(DEFAULT_NAME)
        private set
    var profileEmail by mutableStateOf(DEFAULT_EMAIL)
        private set

    // Choix en cours pendant la commande (non sauvegardés)
    var deliveryMode by mutableStateOf(DeliveryMode.HOME)
    var paymentMode by mutableStateOf(PaymentMode.MOBILE_MONEY) // utilisé seulement si aucun moyen de paiement n'est enregistré
    var selectedPaymentId by mutableStateOf<Long?>(null)
    var lastOrderId by mutableStateOf("")
        private set

    val cartCount: Int get() = cart.sumOf { it.quantity }
    val subtotal: Int get() = cart.sumOf { it.total }
    val deliveryFee: Int get() = if (cart.isEmpty()) 0 else deliveryMode.fee
    val total: Int get() = subtotal + deliveryFee

    val defaultAddress: AddressEntity?
        get() = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()
    val checkoutPayment: PaymentMethodEntity?
        get() = payments.firstOrNull { it.id == selectedPaymentId }
            ?: payments.firstOrNull { it.isDefault } ?: payments.firstOrNull()
    val canPlaceOrder: Boolean
        get() = cart.isNotEmpty() && (deliveryMode == DeliveryMode.PICKUP || defaultAddress != null)

    init {
        // Catalogue embarqué : synchronisé avec Room à chaque lancement (ajouts, modifs et suppressions)
        viewModelScope.launch {
            dao.syncCatalog(
                SampleData.categories.mapIndexed { i, c -> c.toEntity(i) },
                SampleData.products.map { it.copy(isPopular = it.id in SampleData.popularIds).toEntity() },
            )
        }
        viewModelScope.launch { dao.observeCategories().collect { rows -> categories = rows.map { it.toCategory() } } }
        viewModelScope.launch { dao.observeProducts().collect { rows -> products = rows.map { it.toProduct() } } }
        viewModelScope.launch {
            combine(dao.observeCart(), dao.observeProducts()) { rows, prods ->
                val byId = prods.associateBy { it.id }
                rows.mapNotNull { r -> byId[r.productId]?.let { CartItem(it.toProduct(), r.quantity) } }
            }.collect { cart = it }
        }
        viewModelScope.launch {
            dao.observeFavorites().collect { rows -> favorites = rows.map { it.productId }.toSet() }
        }
        viewModelScope.launch { dao.observeOrders().collect { orders = it } }
        viewModelScope.launch { dao.observeAddresses().collect { addresses = it } }
        viewModelScope.launch { dao.observePayments().collect { payments = it } }
        viewModelScope.launch {
            dao.observeSettings().collect { rows ->
                profileName = rows.find { it.key == KEY_NAME }?.value ?: DEFAULT_NAME
                profileEmail = rows.find { it.key == KEY_EMAIL }?.value ?: DEFAULT_EMAIL
            }
        }
    }

    // Panier
    fun add(product: Product, qty: Int = 1) = viewModelScope.launch {
        val current = dao.cartItem(product.id)?.quantity ?: 0
        dao.upsertCart(CartEntity(product.id, current + qty))
    }

    fun setQty(productId: Int, qty: Int) = viewModelScope.launch {
        dao.upsertCart(CartEntity(productId, qty.coerceAtLeast(1)))
    }

    fun remove(productId: Int) = viewModelScope.launch { dao.deleteCart(productId) }

    // Favoris
    fun toggleFavorite(productId: Int) = viewModelScope.launch {
        if (productId in favorites) dao.removeFavorite(productId) else dao.addFavorite(FavoriteEntity(productId))
    }

    // Adresses
    fun saveAddress(address: AddressEntity) = viewModelScope.launch { dao.saveAddress(address) }
    fun deleteAddress(id: Long) = viewModelScope.launch { dao.deleteAddress(id) }
    fun setDefaultAddress(id: Long) = viewModelScope.launch { dao.makeAddressDefault(id) }

    // Moyens de paiement
    fun savePayment(payment: PaymentMethodEntity) = viewModelScope.launch { dao.savePayment(payment) }
    fun deletePayment(id: Long) = viewModelScope.launch {
        if (selectedPaymentId == id) selectedPaymentId = null
        dao.deletePayment(id)
    }
    fun setDefaultPayment(id: Long) = viewModelScope.launch { dao.makePaymentDefault(id) }

    // Profil
    fun saveProfile(name: String, email: String) = viewModelScope.launch {
        dao.upsertSetting(SettingEntity(KEY_NAME, name.ifBlank { DEFAULT_NAME }))
        dao.upsertSetting(SettingEntity(KEY_EMAIL, email.ifBlank { DEFAULT_EMAIL }))
    }

    /** Pas de compte en ligne : "se déconnecter" efface toutes les données locales de l'utilisateur. */
    fun logout() = viewModelScope.launch {
        selectedPaymentId = null
        dao.clearUserData()
    }

    // Commande
    fun placeOrder() {
        val items = cart
        if (!canPlaceOrder) return
        val code = "#SC" + (System.currentTimeMillis() % 100000000L).toString().padStart(8, '0')
        lastOrderId = code
        val order = OrderEntity(
            code = code,
            deliveryMode = deliveryMode.name,
            paymentMode = checkoutPayment?.type ?: paymentMode.name,
            address = if (deliveryMode == DeliveryMode.HOME) defaultAddress?.details.orEmpty() else "Retrait en magasin",
            deliveryFee = deliveryFee,
            subtotal = subtotal,
            total = total,
            createdAt = System.currentTimeMillis(),
        )
        val lines = items.map {
            OrderItemEntity(orderId = 0, productId = it.product.id, productName = it.product.name,
                unitPrice = it.product.price, quantity = it.quantity)
        }
        viewModelScope.launch { dao.placeOrder(order, lines) }
    }

    private companion object {
        const val KEY_NAME = "profile_name"
        const val KEY_EMAIL = "profile_email"
        const val DEFAULT_NAME = "Invité"
        const val DEFAULT_EMAIL = "Touchez pour ajouter votre e-mail"
    }
}
