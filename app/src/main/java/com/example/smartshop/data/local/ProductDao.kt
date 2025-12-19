package com.example.smartshop.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM product_images WHERE productId = :productId")
    suspend fun getImage(productId: String): ProductImageEntity?

    @Query("SELECT * FROM product_images")
    fun getAllImages(): Flow<List<ProductImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ProductImageEntity)

    @Delete
    suspend fun deleteImage(image: ProductImageEntity)

    @Query("DELETE FROM product_images WHERE productId = :productId")
    suspend fun deleteImageById(productId: String)
}
