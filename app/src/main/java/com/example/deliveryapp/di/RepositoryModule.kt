package com.example.deliveryapp.di

import com.example.deliveryapp.data.repository.DeliveryRepositoryImpl
import com.example.deliveryapp.domain.repository.DeliveryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDeliveryRepository(
        impl: DeliveryRepositoryImpl
    ): DeliveryRepository
}