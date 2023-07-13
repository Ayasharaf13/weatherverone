package com.example.weathermate.database

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.weather_data_fetcher.CurrentForecast
import com.example.weathermate.weather_data_fetcher.CurrentWeather
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*


@RunWith(JUnit4::class)
class ConcreteLocalSourceTest {

    private lateinit var concreteLocalSource: ConcreteLocalSource
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        weatherDao = mock(WeatherDao::class.java)
        concreteLocalSource = ConcreteLocalSource(weatherDao)
    }

    @Test
    fun insertNewFavorite_callsWeatherDaoInsertNewFavorite() = runBlocking {
        val weatherDao = mock(WeatherDao::class.java)
        val favoriteWeatherResponse = mock(FavoriteWeatherResponse::class.java)
        val concreteLocalSource = ConcreteLocalSource(weatherDao)

        concreteLocalSource.insertNewFavorite(favoriteWeatherResponse)

        verify(weatherDao).insertNewFavorite(favoriteWeatherResponse)
    }


    @Test
    fun deleteFavorite_callsWeatherDaoDeleteFavorite() = runBlocking {
        val favoriteWeatherResponse = mock(FavoriteWeatherResponse::class.java)

        concreteLocalSource.deleteFavorite(favoriteWeatherResponse)

        verify(weatherDao).deleteFavorite(favoriteWeatherResponse)
    }

    @Test
    fun getAllAlerts_callsWeatherDaoGetAllAlerts() = runBlocking {
        val mockFlow = flow<List<RoomAlertPojo>> { }

        `when`(weatherDao.getAllAlerts()).thenReturn(mockFlow)

        val result = concreteLocalSource.getAllAlerts()

        verify(weatherDao).getAllAlerts()
        assertEquals(mockFlow, result)
    }

    @Test
    fun insertAlert_callsWeatherDaoInsertAlert() = runBlocking {
        val alert = mock(RoomAlertPojo::class.java)

        concreteLocalSource.insertAlert(alert)

        verify(weatherDao).insertAlert(alert)
    }

    @Test
    fun deleteAlert_callsWeatherDaoDeleteAlert() = runBlocking {
        val alert = mock(RoomAlertPojo::class.java)

        concreteLocalSource.deleteAlert(alert)

        verify(weatherDao).deleteAlert(alert)
    }

    @Test
    fun getWeatherDetails_callsWeatherDaoGetWeatherDetails() {
        val mockFlow = flow<List<WeatherResponse>> { }

        `when`(weatherDao.getWeatherDetails()).thenReturn(mockFlow)

        val result = concreteLocalSource.getWeatherDetails()

        verify(weatherDao).getWeatherDetails()
        assertEquals(mockFlow, result)
    }

    @Test
    fun getLocalFavDetails_callsWeatherDaoGetLocalFavDetails() {
        val mockFlow = flow<List<FavoriteWeatherResponse>> { }

        `when`(weatherDao.getLocalFavDetails()).thenReturn(mockFlow)

        val result = concreteLocalSource.getLocalFavDetails()

        verify(weatherDao).getLocalFavDetails()
        assertEquals(mockFlow, result)
    }
}

/*
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ConcreteLocalSourceTest {
    private lateinit var database: WeatherDB
    private lateinit var localSource: LocalSource
    private lateinit var favoriteWeatherResponse : FavoriteWeatherResponse
    private lateinit var favoriteWeatherResponse2 : FavoriteWeatherResponse

 */
/*
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDB::class.java
        ).allowMainThreadQueries().build()

        localSource = ConcreteLocalSource(database.getWeatherDao())

        favoriteWeatherResponse = FavoriteWeatherResponse(
            latitude = 40.7128,
            longitude = -74.0060,
            cityName = "New York",
            temp = 17.0,
            dt = 1618242884L,
            img = "01d",
            description = "clear sky"
        )
        favoriteWeatherResponse2 = FavoriteWeatherResponse(
            latitude = 40.7128,
            longitude = -74.0060,
            cityName = "Alex",
            temp = 17.0,
            dt = 1618242884L,
            img = "01d",
            description = "clear sky"
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavoriteWeatherResponse_favoriteWeatherResponse_OneInserted() = runBlockingTest {
        //sets up the 1 object to insert
        val favoriteWeatherResponse = FavoriteWeatherResponse(
            latitude = 40.7128,
            longitude = -74.0060,
            cityName = "New York",
            temp = 17.0,
            dt = 1618242884L,
            img = "01d",
            description = "clear sky"
        )


        localSource.insertNewFavorite(favoriteWeatherResponse)

        //when getting list of data from room
        val allWeatherDetailsList = localSource.getLocalFavDetails()

        //then testing that list returned from room has only the second weatherResponse
        MatcherAssert.assertThat(allWeatherDetailsList.get(0).description, `is`("clear sky"))
    }

 */
    /*

    @Test
    fun insertTwoFavoriteWeatherResponse_favoriteWeatherResponse_twoItemsInList() =
        runBlockingTest {
            //sets up the 2 object to insert in setup()

            localSource.insertNewFavorite(favoriteWeatherResponse)
            localSource.insertNewFavorite(favoriteWeatherResponse2)

            //when getting list of data from room
            val allFavs = localSource.getLocalFavDetails()

            //then testing that list returned zero
            Log.i(
                "TAG",
                "insertFavoriteWeatherResponseAndDeleteIt_favoriteWeatherResponse_zeroItemsInList: ${allFavs.size}"
            )
            assertThat(allFavs.size, `is`(2))
            assertThat(allFavs.get(0).cityName, `is`("New York"))
            assertThat(allFavs.get(1).cityName, `is`("Alex"))
        }

    @Test
    fun insertWeatherDetails_weatherResponse_OneInserted() = runBlockingTest{
        //sets up the object to insert
        val weatherResponse = WeatherResponse(
            id = 1,
            cityLatitude = 40.7128,
            cityLongitude = -74.0060,
            locationName = "New York",
            timezone_offset = -14400,
            currentForecast = CurrentForecast(
                time = 1618242884,
                sunrise = 1618206314,
                sunset = 1618251784,
                temp = 17.0,
                feelsLike = 16.0,
                pressure = 1012,
                humidity = 70,
                atmosphericTemp = 10.0,
                uvi = 0.0,
                clouds = 20,
                visibility = 10000,
                windSpeed = 3.6,
                windGust = null,
                windDeg = 150,
                rain = null,
                snow = null,
                weather = listOf(
                    CurrentWeather(
                        id = 800,
                        main = "Clear",
                        description = "clear sky",
                        icon = "01d"
                    )
                )
            ),
            hourlyForecast = listOf(),
            dailyForecast = listOf()
        )

        localSource.insertWeatherDetails(weatherResponse)


        //when getting list of data from room
        var allWeatherDetailsList = localSource.getWeatherDetails()

        //then testing that list returned from room has one item
        assertThat(allWeatherDetailsList.get(0), `is`(weatherResponse))
        assertThat(
            allWeatherDetailsList.get(0).locationName,
            `is`(weatherResponse.locationName)
        )
        assertThat(
            allWeatherDetailsList.get(0).currentForecast?.feelsLike,
            `is`(weatherResponse.currentForecast?.feelsLike)
        )
    }

     */

