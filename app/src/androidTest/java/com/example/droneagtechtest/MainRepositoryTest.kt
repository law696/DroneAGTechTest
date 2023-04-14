package com.example.droneagtechtest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.droneagtechtest.model.MainRepository
import com.example.droneagtechtest.model.db.PolygonEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MainRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainRepository: MainRepository

    private val polygonEntity = PolygonEntity(
        id = 1L,
        points = "10.0,20.0;30.0,40.0",
        centerPoint = "15.0,30.0",
        area = "1000.0")

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mainRepository = MainRepository()
    }

    @Test
    fun testInsertPolygon() = runBlocking {
        mainRepository.clearAll()
        mainRepository.insert(polygonEntity)
        assertEquals(1, mainRepository.getCount())
    }
}