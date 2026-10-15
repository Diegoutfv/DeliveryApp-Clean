package com.example.deliveryapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.deliveryapp.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE dishId = :dishId LIMIT 1")
    suspend fun getCartItemById(dishId: Int): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE dishId = :dishId")
    suspend fun deleteCartItem(dishId: Int)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE dishId = :dishId")
    suspend fun updateQuantity(dishId: Int, quantity: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}