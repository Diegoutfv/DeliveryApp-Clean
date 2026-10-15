package com.example.deliveryapp.di

import android.content.Context
import androidx.room.Room
import com.example.deliveryapp.data.local.dao.CartItemDao
import com.example.deliveryapp.data.local.dao.DishDao
import com.example.deliveryapp.data.local.database.DeliveryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): DeliveryDatabase {
        return Room.databaseBuilder(
            context,
            DeliveryDatabase::class.java,
            DeliveryDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideDishDao(database: DeliveryDatabase): DishDao {
        return database.dishDao()
    }

    @Provides
    @Singleton
    fun provideCartItemDao(database: DeliveryDatabase): CartItemDao {
        return database.cartItemDao()
    }
}