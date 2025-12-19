package com.example.smartshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_images")
data class ProductImageEntity(
    @PrimaryKey val productId: String,
    val imageUri: String
)

