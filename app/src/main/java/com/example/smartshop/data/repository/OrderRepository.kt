package com.example.smartshop.data.repository

import com.example.smartshop.data.cloud.OrderFirestore
import com.example.smartshop.data.cloud.OrderRemote
import com.example.smartshop.data.toOrder
import com.example.smartshop.data.toOrderRemote
import com.example.smartshop.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrderRepository(
    private val cloud: OrderFirestore
) {
    fun getAll(): Flow<List<Order>> {
        return cloud.observeAll().map { remoteOrders ->
            remoteOrders.map { remote ->
                remote.toOrder()
            }
        }
    }

    suspend fun create(order: Order): Result<String> {
        return cloud.create(order.toOrderRemote())
    }
}

