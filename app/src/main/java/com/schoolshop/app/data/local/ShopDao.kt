package com.schoolshop.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ShopDao {
    // Catalogue
    @Query("SELECT * FROM categories ORDER BY position")
    abstract fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM products ORDER BY id")
    abstract fun observeProducts(): Flow<List<ProductEntity>>

    @Upsert
    abstract suspend fun upsertCategories(items: List<CategoryEntity>)

    @Upsert
    abstract suspend fun upsertProducts(items: List<ProductEntity>)

    // Nettoyage : supprime ce qui n'est plus dans le catalogue embarqué
    @Query("DELETE FROM products WHERE id NOT IN (:ids)")
    abstract suspend fun deleteProductsNotIn(ids: List<Int>)

    @Query("DELETE FROM categories WHERE id NOT IN (:ids)")
    abstract suspend fun deleteCategoriesNotIn(ids: List<String>)

    @Query("DELETE FROM cart_items WHERE productId NOT IN (:ids)")
    abstract suspend fun deleteCartNotIn(ids: List<Int>)

    @Query("DELETE FROM favorites WHERE productId NOT IN (:ids)")
    abstract suspend fun deleteFavoritesNotIn(ids: List<Int>)

    /** Synchronise la base avec le catalogue embarqué : ajoute, met à jour et supprime. */
    @Transaction
    open suspend fun syncCatalog(categories: List<CategoryEntity>, products: List<ProductEntity>) {
        upsertCategories(categories)
        upsertProducts(products)
        val productIds = products.map { it.id }
        deleteProductsNotIn(productIds)
        deleteCategoriesNotIn(categories.map { it.id })
        deleteCartNotIn(productIds)
        deleteFavoritesNotIn(productIds)
    }

    // Adresses
    @Query("SELECT * FROM addresses ORDER BY isDefault DESC, id")
    abstract fun observeAddresses(): Flow<List<AddressEntity>>

    @Upsert
    abstract suspend fun upsertAddress(a: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :id")
    abstract suspend fun deleteAddressRow(id: Long)

    @Query("UPDATE addresses SET isDefault = 0")
    abstract suspend fun clearDefaultAddress()

    @Query("UPDATE addresses SET isDefault = 1 WHERE id = :id")
    abstract suspend fun setAddressDefault(id: Long)

    @Query("UPDATE addresses SET isDefault = 1 WHERE id = (SELECT id FROM addresses ORDER BY id LIMIT 1) AND NOT EXISTS (SELECT 1 FROM addresses WHERE isDefault = 1)")
    abstract suspend fun ensureDefaultAddress()

    @Transaction
    open suspend fun saveAddress(a: AddressEntity) {
        if (a.isDefault) clearDefaultAddress()
        upsertAddress(a)
        ensureDefaultAddress()
    }

    @Transaction
    open suspend fun deleteAddress(id: Long) {
        deleteAddressRow(id)
        ensureDefaultAddress()
    }

    @Transaction
    open suspend fun makeAddressDefault(id: Long) {
        clearDefaultAddress()
        setAddressDefault(id)
    }

    // Moyens de paiement
    @Query("SELECT * FROM payment_methods ORDER BY isDefault DESC, id")
    abstract fun observePayments(): Flow<List<PaymentMethodEntity>>

    @Upsert
    abstract suspend fun upsertPayment(p: PaymentMethodEntity)

    @Query("DELETE FROM payment_methods WHERE id = :id")
    abstract suspend fun deletePaymentRow(id: Long)

    @Query("UPDATE payment_methods SET isDefault = 0")
    abstract suspend fun clearDefaultPayment()

    @Query("UPDATE payment_methods SET isDefault = 1 WHERE id = :id")
    abstract suspend fun setPaymentDefault(id: Long)

    @Query("UPDATE payment_methods SET isDefault = 1 WHERE id = (SELECT id FROM payment_methods ORDER BY id LIMIT 1) AND NOT EXISTS (SELECT 1 FROM payment_methods WHERE isDefault = 1)")
    abstract suspend fun ensureDefaultPayment()

    @Transaction
    open suspend fun savePayment(p: PaymentMethodEntity) {
        if (p.isDefault) clearDefaultPayment()
        upsertPayment(p)
        ensureDefaultPayment()
    }

    @Transaction
    open suspend fun deletePayment(id: Long) {
        deletePaymentRow(id)
        ensureDefaultPayment()
    }

    @Transaction
    open suspend fun makePaymentDefault(id: Long) {
        clearDefaultPayment()
        setPaymentDefault(id)
    }

    // Effacement de toutes les données de l'utilisateur (déconnexion)
    @Query("DELETE FROM favorites") abstract suspend fun clearFavorites()
    @Query("DELETE FROM orders") abstract suspend fun clearOrders()
    @Query("DELETE FROM addresses") abstract suspend fun clearAddresses()
    @Query("DELETE FROM payment_methods") abstract suspend fun clearPayments()
    @Query("DELETE FROM settings") abstract suspend fun clearSettings()

    @Transaction
    open suspend fun clearUserData() {
        clearCart(); clearFavorites(); clearOrders(); clearAddresses(); clearPayments(); clearSettings()
    }

    // Panier
    @Query("SELECT * FROM cart_items")
    abstract fun observeCart(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :id")
    abstract suspend fun cartItem(id: Int): CartEntity?

    @Upsert
    abstract suspend fun upsertCart(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE productId = :id")
    abstract suspend fun deleteCart(id: Int)

    @Query("DELETE FROM cart_items")
    abstract suspend fun clearCart()

    // Favoris
    @Query("SELECT * FROM favorites")
    abstract fun observeFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addFavorite(f: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :id")
    abstract suspend fun removeFavorite(id: Int)

    // Réglages (adresse de livraison...)
    @Query("SELECT * FROM settings")
    abstract fun observeSettings(): Flow<List<SettingEntity>>

    @Upsert
    abstract suspend fun upsertSetting(s: SettingEntity)

    // Commandes
    @Insert
    abstract suspend fun insertOrder(o: OrderEntity): Long

    @Insert
    abstract suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    abstract fun observeOrders(): Flow<List<OrderWithItems>>

    /** Enregistre la commande et vide le panier en une seule transaction. */
    @Transaction
    open suspend fun placeOrder(order: OrderEntity, items: List<OrderItemEntity>) {
        val id = insertOrder(order)
        insertOrderItems(items.map { it.copy(orderId = id) })
        clearCart()
    }
}
