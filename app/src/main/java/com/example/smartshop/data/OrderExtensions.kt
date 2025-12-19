package com.example.smartshop.data

import com.example.smartshop.data.cloud.OrderRemote
import com.example.smartshop.model.Order

fun Order.toOrderRemote(): OrderRemote {
    return OrderRemote(
        id = this.id,
        productId = this.productId,
        productName = this.productName,
        userId = this.userId,
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        totalPrice = this.totalPrice,
        status = this.status,
        createdAt = this.createdAt
    )
}

fun OrderRemote.toOrder(): Order {
    return Order(
        id = this.id,
        productId = this.productId,
        productName = this.productName,
        userId = this.userId,
        quantity = this.quantity,
        unitPrice = this.unitPrice,
        totalPrice = this.totalPrice,
        status = this.status,
        createdAt = this.createdAt
    )
}

