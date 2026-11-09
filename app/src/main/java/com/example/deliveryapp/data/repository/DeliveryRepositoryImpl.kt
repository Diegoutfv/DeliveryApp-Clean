package com.example.deliveryapp.data.repository

import com.example.deliveryapp.data.local.dao.CartItemDao
import com.example.deliveryapp.data.local.dao.DishDao
import com.example.deliveryapp.data.local.entity.CartItemEntity
import com.example.deliveryapp.data.local.entity.DishEntity
import com.example.deliveryapp.domain.model.CartItem
import com.example.deliveryapp.domain.model.Dish
import com.example.deliveryapp.domain.model.DishCategory
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
        val pizza = DishCategory.PIZZA.name
        val drink = DishCategory.DRINK.name
        val initialDishes = listOf(
            DishEntity(101, "Rossa Clásica", "Salsa de tomate, mozzarella fresca y albahaca", 189.0, "", pizza),
            DishEntity(102, "Pepperoni Fuego", "Pepperoni artesanal, mozzarella y orégano", 199.0, "", pizza),
            DishEntity(103, "Cuatro Quesos", "Mozzarella, gorgonzola, parmesano y provolone", 219.0, "", pizza),
            DishEntity(104, "Diavola", "Salami picante, chile calabrés y miel", 209.0, "", pizza),
            DishEntity(105, "Hawaiana Tropical", "Jamón, piña asada y mozzarella", 189.0, "", pizza),
            DishEntity(106, "Verde Hortelana", "Pimiento, champiñón, aceituna y rúcula", 179.0, "", pizza),
            DishEntity(201, "Limonada Rosa", "Limón natural con frambuesa y hielo", 45.0, "", drink),
            DishEntity(202, "Cola Clásica", "Refresco de cola bien frío, 500 ml", 38.0, "", drink),
            DishEntity(203, "Té Helado de Durazno", "Té negro, durazno y un toque de menta", 42.0, "", drink),
            DishEntity(204, "Naranjada Fresca", "Jugo de naranja exprimido al momento", 45.0, "", drink),
            DishEntity(205, "Agua Mineral", "Agua con gas, 500 ml", 32.0, "", drink),
            DishEntity(206, "Cerveza Artesanal Roja", "Ale ámbar de la casa, 355 ml", 69.0, "", drink)
        )
        // Reemplaza el menú anterior para que no queden platillos viejos
        dishDao.clearDishes()
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
    imageUrl = imageUrl,
    category = runCatching { DishCategory.valueOf(category) }.getOrDefault(DishCategory.PIZZA)
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