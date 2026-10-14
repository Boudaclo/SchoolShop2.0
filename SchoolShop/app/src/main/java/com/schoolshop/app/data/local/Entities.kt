package com.schoolshop.app.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "cart_items")
data class CartEntity(@PrimaryKey val productId: Int, val quantity: Int)

@Entity(tableName = "favorites")
data class FavoriteEntity(@PrimaryKey val productId: Int)

@Entity(tableName = "settings")
data class SettingEntity(@PrimaryKey val key: String, val value: String)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val deliveryMode: String,
    val paymentMode: String,
    val address: String,
    val deliveryFee: Int,
    val subtotal: Int,
    val total: Int,
    val createdAt: Long,
)

@Entity(
    tableName = "order_items",
    foreignKeys = [ForeignKey(
        entity = OrderEntity::class,
        parentColumns = ["id"],
        childColumns = ["orderId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("orderId")],
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val productId: Int,
    val productName: String,
    val unitPrice: Int,
    val quantity: Int,
)

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(parentColumn = "id", entityColumn = "orderId") val items: List<OrderItemEntity>,
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val subtitle: String,
    val emoji: String,
    val position: Int,
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val categoryId: String,
    val name: String,
    val format: String,
    val price: Int,
    val emoji: String,
    val rating: Float,
    val reviews: Int,
    val description: String,
    val features: String, // caractéristiques séparées par des retours à la ligne
    val isPopular: Boolean,
    val imageUrl: String?,
)
