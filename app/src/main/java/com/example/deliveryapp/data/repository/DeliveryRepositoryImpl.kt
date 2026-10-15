package com.example.deliveryapp.data.repository

import com.example.deliveryapp.data.local.dao.CartItemDao
import com.example.deliveryapp.data.local.dao.DishDao
import com.example.deliveryapp.data.local.entity.CartItemEntity
import com.example.deliveryapp.data.local.entity.DishEntity
import com.example.deliveryapp.domain.model.CartItem
import com.example.deliveryapp.domain.model.Dish
import com.example.deliveryapp.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val dishDao: DishDao,
    private val cartItemDao: CartItemDao
) : DeliveryRepository {

    override fun getDishes(): Flow<List<Dish>> {
        return dishDao.getAllDishes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun loadDishesFromApi() {
        // Datos precargados localmente (por ahora, sin Retrofit)
        val initialDishes = listOf(
            DishEntity(1, "Pizza Margarita", "Deliciosa pizza clásica", 12.5, ""),
            DishEntity(2, "Hamburguesa", "Carne jugosa con queso", 8.0, ""),
            DishEntity(3, "Tacos al Pastor", "Tacos tradicionales", 6.5, ""),
            DishEntity(4, "Refresco", "Bebida fría", 2.0, ""),
            DishEntity(5, "Postre", "Dulce tentación", 4.5, ""),
            DishEntity(6, "Ensalada César", "Fresca y saludable", 7.0, "")
        )
        dishDao.insertDishes(initialDishes)
    }

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartItemDao.getAllCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addToCart(dish: Dish) {
        val existing = cartItemDao.getCartItemById(dish.id)
        if (existing == null) {
            cartItemDao.insertCartItem(
                CartItemEntity(
                    dishId = dish.id,
                    dishName = dish.name,
                    dishPrice = dish.price,
                    quantity = 1
                )
            )
        } else {
            cartItemDao.updateQuantity(dish.id, existing.quantity + 1)
        }
    }

    override suspend fun increaseQuantity(dishId: Int) {
        val existing = cartItemDao.getCartItemById(dishId) ?: return
        cartItemDao.updateQuantity(dishId, existing.quantity + 1)
    }

    override suspend fun decreaseQuantity(dishId: Int) {
        val existing = cartItemDao.getCartItemById(dishId) ?: return
        if (existing.quantity <= 1) {
            cartItemDao.deleteCartItem(dishId)
        } else {
            cartItemDao.updateQuantity(dishId, existing.quantity - 1)
        }
    }

    override suspend fun removeFromCart(dishId: Int) {
        cartItemDao.deleteCartItem(dishId)
    }

    override suspend fun clearCart() {
        cartItemDao.clearCart()
    }
}

// ============================================
// Mappers: Entity (data) → Model (domain)
// ============================================

private fun DishEntity.toDomain() = Dish(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl
)

private fun CartItemEntity.toDomain() = CartItem(
    dish = Dish(
        id = dishId,
        name = dishName,
        description = "",
        price = dishPrice,
        imageUrl = ""
    ),
    quantity = quantity
)