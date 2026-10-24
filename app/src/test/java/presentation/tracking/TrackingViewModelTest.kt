package com.example.deliveryapp.presentation.tracking

import com.example.deliveryapp.data.repository.TrackingRepository
import com.example.deliveryapp.domain.model.DeliveryOrder
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrackingViewModelTest {

    private val repository: TrackingRepository = mockk(relaxed = true)
    private lateinit var viewModel: TrackingViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al rastrear pedido debe emitir las coordenadas recibidas`() = runTest {
        // 1. Datos ficticios
        val fakeOrder = DeliveryOrder(
            orderId = "123",
            driverName = "Carlos",
            lat = 19.43,
            lng = -99.13,
            status = "IN_DELIVERY"
        )

        // 2. Configurar el mock
        coEvery { repository.getLiveLocation("123") } returns flowOf(fakeOrder)

        // 3. Instanciar el ViewModel
        viewModel = TrackingViewModel(repository)

        // 4. Ejecutar la función
        viewModel.trackOrder("123")

        // 5. Esperar a que las corrutinas terminen
        advanceUntilIdle()

        // 6. Verificar resultados
        assertEquals(19.43, viewModel.orderState.value.lat, 0.01)
        assertEquals("Carlos", viewModel.orderState.value.driverName)
    }
}