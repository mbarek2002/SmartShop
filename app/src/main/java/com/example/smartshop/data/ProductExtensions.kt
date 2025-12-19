package com.example.smartshop.data

import com.example.smartshop.data.cloud.ProductRemote
import com.example.smartshop.model.Product
import com.google.firebase.auth.FirebaseAuth

fun Product.toRemote(): ProductRemote {
    return ProductRemote(
        id = this.id,
        name = this.name,
        quantity = this.quantity,
        price = this.price,
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    )
}

fun ProductRemote.toProduct(imageUri: String = ""): Product {
    return Product(
        id = this.id,
        name = this.name,
        quantity = this.quantity,
        price = this.price,
        userId = this.userId
    )
}
