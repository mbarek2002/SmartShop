package com.example.smartshop.data.repository

import com.example.smartshop.data.cloud.ProductFirestore
import com.example.smartshop.data.local.ProductDao
import com.example.smartshop.data.local.ProductImageEntity
import com.example.smartshop.data.toProduct
import com.example.smartshop.data.toRemote
import com.example.smartshop.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(
    private val local: ProductDao,
    private val cloud: ProductFirestore
) {

    fun getAll(): Flow<List<Product>> {
        return cloud.observeAll().map { remoteProducts ->
            remoteProducts.map { remote ->
                remote.toProduct()
            }
        }
    }

    suspend fun add(product: Product, imageUri: String): Result<String> {
        // Save data to Firebase
        val result = cloud.upload(product.toRemote())
        result.getOrElse { throwable ->
            return Result.failure(throwable)
        }
        
        // Save image locally if provided
        if (imageUri.isNotBlank()) {
            try {
                local.insertImage(ProductImageEntity(product.id, imageUri))
            } catch (e: Exception) {
                // Image save failed, but product is saved in Firebase
            }
        }
        return Result.success("Produit ajouté avec succès")
    }

    suspend fun update(product: Product, imageUri: String): Result<String> {
        // Update data in Firebase
        val result = cloud.upload(product.toRemote())
        result.getOrElse { throwable ->
            return Result.failure(throwable)
        }
        
        // Update image locally
        if (imageUri.isNotBlank()) {
            try {
                local.insertImage(ProductImageEntity(product.id, imageUri))
            } catch (e: Exception) {
                // Image save failed, but product is updated in Firebase
            }
        } else {
            // If imageUri is empty, check if we should delete existing image
            try {
                local.deleteImageById(product.id)
            } catch (e: Exception) {
                // Image delete failed, but product is updated in Firebase
            }
        }
        return Result.success("Produit mis à jour avec succès")
    }

    suspend fun delete(product: Product): Result<String> {
        // Delete from Firebase
        val result = cloud.delete(product.id)
        result.getOrElse { throwable ->
            return Result.failure(throwable)
        }
        
        // Delete image from local storage
        try {
            local.deleteImageById(product.id)
        } catch (e: Exception) {
            // Image delete failed, but product is deleted from Firebase
        }
        return Result.success("Produit supprimé avec succès")
    }

    suspend fun getImageUri(productId: String): String? {
        return local.getImage(productId)?.imageUri
    }
}
