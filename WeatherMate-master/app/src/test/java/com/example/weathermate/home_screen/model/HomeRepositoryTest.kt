package com.example.weathermate.home_screen.model

import com.example.weathermate.database.FakeLocalDataSource
import com.example.weathermate.network.FakeRemoteDataSource
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class HomeRepositoryTest {
    private lateinit var remoteSource: FakeRemoteDataSource
    private lateinit var localSource: FakeLocalDataSource
    private lateinit var homeRepository: HomeRepository

    @Before
    fun setup() {
        remoteSource = FakeRemoteDataSource()
        localSource = FakeLocalDataSource()
        homeRepository = HomeRepository(remoteSource, localSource)
    }

    @Test
    fun `getWeatherData should return data from remoteSource`() = runBlockingTest {
        // Set up the arguments for the method call
        //Given
        val latitude = 1.0
        val longitude = 2.0
        val units = "metric"
        val lang = "en"

        // Call the method under test
        //when
        val result = homeRepository.getWeatherData(latitude, longitude, units, lang).single()

        // Verify the expected behavior
        //then
        val expectedResponse = WeatherResponse(cityLatitude = latitude, cityLongitude = longitude)
        Assert.assertEquals(expectedResponse, result.body())
    }
}



/*
    @Test
    fun `getWeatherDetails should return data from localSource`() = runBlockingTest {
        // Set up the weather response list in the local source
        //Given
        val weatherResponseList = mutableListOf(
            WeatherResponse(1),
            WeatherResponse(2),
            WeatherResponse(3)
        )
       // localSource.setWeatherDetails(weatherResponseList)


        // Call the method under test
        //when
        val result = homeRepository.getWeatherDetails().single()

        // Verify the expected behavior
        //Then
        delay(1000)
        Assert.assertEquals(weatherResponseList, result)
    }

 */






/*
class HomeRepositoryTest {
    lateinit var localSource: LocalSource
    lateinit var remoteSource: RemoteSource
    lateinit var repository: HomeRepositoryInterface

    @Before
    fun setup() {
        localSource = FakeLocalDataSource(
            mutableListOf(
                WeatherResponse(1),
                WeatherResponse(2),
                WeatherResponse(3)
            )
        )
        remoteSource = FakeRemoteDataSource()
        repository = FakeHomeRepository(localSource, remoteSource)
    }

    @Test
    fun addNewWeatherDetails_newWeatherDetails_sizeFour() = runBlockingTest {
        //when prepare new item and inserting it
        val weatherResponse4 = WeatherResponse(4)
   //     repository.insertWeatherDetails(weatherResponse4)

        //given get the new list
        val list = repository.getWeatherDetails().first()

        //then last item is the new item
        assertThat(list.last().id, `is`(4))
    }

    @Test
    fun getWeatherDetailsList_zero_sizeThree() = runBlockingTest {
        //when
        val list = repository.getWeatherDetails().first()

        //then
        assertThat(list.size, `is`(3))
    }

    @Test
    fun getOnlineWeatherDetails_latLong_weatherResponse() = runBlockingTest {
        //passing data and saving the response
        val weatherResponse = repository.getWeatherData(
            31.1, 23.1,
            "metric", "en"
        ).first()

        //when
        assertThat(weatherResponse.body()?.cityLatitude,`is`(31.1))
        assertThat(weatherResponse.body()?.cityLongitude,`is`(23.1))
    }

}

 */