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
    var address by mutableStateOf(DEFAULT_ADDRESS)
        private set

    // Choix en cours pendant la commande (non sauvegardés)
    var deliveryMode by mutableStateOf(DeliveryMode.HOME)
    var paymentMode by mutableStateOf(PaymentMode.MOBILE_MONEY)
    var lastOrderId by mutableStateOf("")
        private set

    val cartCount: Int get() = cart.sumOf { it.quantity }
    val subtotal: Int get() = cart.sumOf { it.total }
    val deliveryFee: Int get() = if (cart.isEmpty()) 0 else deliveryMode.fee
    val total: Int get() = subtotal + deliveryFee

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
        viewModelScope.launch {
            dao.observeOrders().collect { orders = it }
        }
        viewModelScope.launch {
            dao.observeSettings().collect { rows ->
                rows.find { it.key == KEY_ADDRESS }?.let { address = it.value }
            }
        }
    }

    fun add(product: Product, qty: Int = 1) = viewModelScope.launch {
        val current = dao.cartItem(product.id)?.quantity ?: 0
        dao.upsertCart(CartEntity(product.id, current + qty))
    }

    fun setQty(productId: Int, qty: Int) = viewModelScope.launch {
        dao.upsertCart(CartEntity(productId, qty.coerceAtLeast(1)))
    }

    fun remove(productId: Int) = viewModelScope.launch { dao.deleteCart(productId) }

    fun toggleFavorite(productId: Int) = viewModelScope.launch {
        if (productId in favorites) dao.removeFavorite(productId) else dao.addFavorite(FavoriteEntity(productId))
    }

    fun saveAddress(text: String) = viewModelScope.launch {
        dao.upsertSetting(SettingEntity(KEY_ADDRESS, text))
    }

    fun placeOrder() {
        val items = cart
        if (items.isEmpty()) return
        val code = "#SC" + (System.currentTimeMillis() % 100000000L).toString().padStart(8, '0')
        lastOrderId = code
        val order = OrderEntity(
            code = code,
            deliveryMode = deliveryMode.name,
            paymentMode = paymentMode.name,
            address = address,
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
        const val KEY_ADDRESS = "address"
        const val DEFAULT_ADDRESS = "1200, Avenue de la Révolution\nOuagadougou, Burkina Faso"
    }
}
