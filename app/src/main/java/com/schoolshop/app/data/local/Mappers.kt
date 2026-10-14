package com.schoolshop.app.data.local

import com.schoolshop.app.data.Category
import com.schoolshop.app.data.Product

fun Category.toEntity(position: Int) = CategoryEntity(id, name, subtitle, emoji, position)
fun CategoryEntity.toCategory() = Category(id, name, subtitle, emoji)

fun Product.toEntity() = ProductEntity(
    id = id, categoryId = categoryId, name = name, format = format, price = price, emoji = emoji,
    rating = rating, reviews = reviews, description = description,
    features = features.joinToString("\n"), isPopular = isPopular, imageUrl = imageUrl,
)

fun ProductEntity.toProduct() = Product(
    id = id, categoryId = categoryId, name = name, format = format, price = price, emoji = emoji,
    rating = rating, reviews = reviews, description = description,
    features = features.split("\n").filter { it.isNotBlank() }, isPopular = isPopular, imageUrl = imageUrl,
)

fun PaymentMethodEntity.summary(): String =
    if (type == "CARD") "$provider •••• $detail" else "$provider · $detail"
