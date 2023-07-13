package com.example.weathermate.favorites.model

import com.example.weathermate.database.FakeLocalDataSource
import com.example.weathermate.network.FakeRemoteDataSource
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class FavoriteRepositoryTest{



    private lateinit var remoteSource: FakeRemoteDataSource
    private lateinit var localSource: FakeLocalDataSource
    private lateinit var favoriteRepository: FavoriteRepository

    @Before
    fun setup() {
        remoteSource = FakeRemoteDataSource()
        localSource = FakeLocalDataSource()
        favoriteRepository = FavoriteRepository(remoteSource, localSource)
    }

    @Test
    fun `getWeatherData should return data from remoteSource`() = runBlockingTest {
        // Given
        val latitude = 1.0
        val longitude = 2.0
        val units = "metric"
        val lang = "en"
        val exclude = "daily"

        // Call the method under test
        val result = favoriteRepository.getWeatherData(latitude, longitude, units, lang, exclude).single()

        // Verify the expected behavior
        val expectedResponse = WeatherResponse(cityLatitude = latitude, cityLongitude = longitude)
        Assert.assertEquals(expectedResponse, result.body())
    }




/*
    @Test
    fun `getLocalFavDetails should return data from localSource`() = runBlockingTest {
        // Given
        val expectedList = mutableListOf(
            FavoriteWeatherResponse(
                id = 1,
                cityName = "New York",
                description = "Sunny"
            ),
            FavoriteWeatherResponse(
                id = 2,
                cityName = "London",
                description = "Cloudy"
            ),
            FavoriteWeatherResponse(
                id = 3,
                cityName = "Tokyo",
                description = "Rainy"
            )
        )
        localSource.favoriteWeatherResponseList = expectedList

        // Call the method under test
        val result = favoriteRepository.getLocalFavDetails().single()

        // Verify the expected behavior
        Assert.assertEquals(expectedList, result)
    }

 */


    @Test
    fun `insertNewFavorite should add the favoriteWeatherResponse to localSource`() = runBlockingTest {
        // Given
        val favoriteWeatherResponse = FavoriteWeatherResponse(
            id = 1,
            cityName = "New York",

            description = "Sunny"
        )

        // Call the method under test
        favoriteRepository.insertNewFavorite(favoriteWeatherResponse)

        // Verify the expected behavior
        val result = localSource.favoriteWeatherResponseList.contains(favoriteWeatherResponse)
        Assert.assertTrue(result)
    }


    @Test
    fun `deleteFavorite should remove the favoriteWeatherResponse from localSource`() = runBlockingTest {
        // Given
        val favoriteWeatherResponse = FavoriteWeatherResponse(
            id = 1,
            cityName = "New York",

            description = "Sunny"
        )
        localSource.favoriteWeatherResponseList.add(favoriteWeatherResponse)

        // Call the method under test
        favoriteRepository.deleteFavorite(favoriteWeatherResponse)

        // Verify the expected behavior
        val result = !localSource.favoriteWeatherResponseList.contains(favoriteWeatherResponse)
        Assert.assertTrue(result)
    }








/*

    @Test
    fun `deleteFavorite should remove the favoriteWeatherResponse from localSource`() = runBlockingTest {
        // Given
        val favoriteWeatherResponse = FavoriteWeatherResponse(/* initialize with necessary data */)
        localSource.favoriteWeatherResponseList.add(favoriteWeatherResponse)

        // Call the method under test
        favoriteRepository.deleteFavorite(favoriteWeatherResponse)

        // Verify the expected behavior
        val result = !localSource.favoriteWeatherResponseList.contains(favoriteWeatherResponse)
        Assert.assertTrue(result)
    }

 */
}