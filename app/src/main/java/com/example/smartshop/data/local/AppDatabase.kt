package com.example.smartshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProductImageEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
