package com.example.weathermate.favorites.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weathermate.database.FakeLocalDataSource
import com.example.weathermate.database.LocalSource
import com.example.weathermate.favorites.model.FavoriteRepository
import com.example.weathermate.favorites.model.FavoriteRepositoryInterface
import com.example.weathermate.home_screen.viewmodel.FakeFavoriteRepository
import com.example.weathermate.network.FakeRemoteDataSource
import com.example.weathermate.network.RemoteSource
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class FavoriteViewModelTest {
        private lateinit var favoriteViewModel: FavoriteViewModel
        private lateinit var repository: FavoriteRepositoryInterface

        // Provide a TestCoroutineDispatcher
        private val testDispatcher = TestCoroutineDispatcher()

        @get:Rule
        val rule = InstantTaskExecutorRule()

        @Before
        fun setup() {
            val localSource: LocalSource = FakeLocalDataSource()
            val remoteSource: RemoteSource = FakeRemoteDataSource()
            repository = FakeFavoriteRepository(localSource, remoteSource)
            favoriteViewModel = FavoriteViewModel(repository)

            // Initialize the Main dispatcher
            Dispatchers.setMain(testDispatcher)
        }

        @After
        fun tearDown() {
            // Reset the Main dispatcher after the test
            Dispatchers.resetMain()
            // Cleanup the TestCoroutineDispatcher
            testDispatcher.cleanupTestCoroutines()
        }

        @Test
        fun testInsertNewFavorite() = testDispatcher.runBlockingTest {
            // Mock data
            val favoriteWeatherResponse = FavoriteWeatherResponse()

            // Call the method to be tested
            favoriteViewModel.insertNewFavorite(favoriteWeatherResponse)

            // Delay to allow coroutines to execute
            advanceTimeBy(1000)

            // Perform assertions on the state flow or other expectations
            // ...
        }

        @Test
        fun testDeleteFavorite() = testDispatcher.runBlockingTest {
            // Mock data
            val favoriteWeatherResponse = FavoriteWeatherResponse()

            // Call the method to be tested
            favoriteViewModel.deleteFavorite(favoriteWeatherResponse)

            // Delay to allow coroutines to execute
            advanceTimeBy(1000)

            // Perform assertions on the state flow or other expectations
            // ...
        }
    }


