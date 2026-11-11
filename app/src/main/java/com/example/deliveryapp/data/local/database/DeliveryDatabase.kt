package com.example.deliveryapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.deliveryapp.data.local.dao.CartItemDao
import com.example.deliveryapp.data.local.dao.DishDao
import com.example.deliveryapp.data.local.dao.OrderDao
import com.example.deliveryapp.data.local.entity.CartItemEntity
import com.example.deliveryapp.data.local.entity.DishEntity
import com.example.deliveryapp.data.local.entity.OrderEntity

@Database(
    entities = [DishEntity::class, CartItemEntity::class, OrderEntity::class],
    version = 4,
    exportSchema = false
)
abstract class DeliveryDatabase : RoomDatabase() {

    abstract fun dishDao(): DishDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun orderDao(): OrderDao

    companion object {
        const val DATABASE_NAME = "delivery_database"
    }
}