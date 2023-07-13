package com.example.weathermate.home_screen.viewmodel



import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.ListenableWorker.Result.success
import com.example.weathermate.database.FakeLocalDataSource
import com.example.weathermate.database.LocalSource
import com.example.weathermate.home_screen.model.FakeHomeRepository
import com.example.weathermate.home_screen.model.HomeRepositoryInterface
import com.example.weathermate.network.ApiState
import com.example.weathermate.network.FakeRemoteDataSource
import com.example.weathermate.network.RemoteSource
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import com.google.common.base.CharMatcher.`is`
import com.google.common.base.Predicates.instanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.Response
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class HomeViewModelTest {

}


/*
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weathermate.database.FakeLocalDataSource
import com.example.weathermate.database.LocalSource
import com.example.weathermate.home_screen.model.FakeHomeRepository
import com.example.weathermate.home_screen.model.HomeRepositoryInterface
import com.example.weathermate.network.ApiState
import com.example.weathermate.network.FakeRemoteDataSource
import com.example.weathermate.network.RemoteSource
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)

class HomeViewModelTest {

    lateinit var localSource: LocalSource
    lateinit var remoteSource: RemoteSource
    lateinit var repository: HomeRepositoryInterface
    lateinit var homeViewModel: HomeViewModel

 var   latitude: Double=0.0
 var   longitude: Double=0.0
     var  units: String=""
     var  lang: String=""

    @get:Rule
    var rule = InstantTaskExecutorRule()//code runs sequentially even if there are threads



    @Before
    fun setup() {
        localSource = FakeLocalDataSource(
            mutableListOf(
                FavoriteWeatherResponse(1),
                FavoriteWeatherResponse(2),
                FavoriteWeatherResponse(3)
            )
        )
        remoteSource = FakeRemoteDataSource()
        repository = FakeHomeRepository(localSource, remoteSource)
        homeViewModel = HomeViewModel(repository)
    }

/*
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
        homeViewModel = HomeViewModel(repository)
    }

 */

    @Test
    fun addNewWeatherDetails_newWeatherDetails_sizeFour() = runBlockingTest {
        //when prepare new item and inserting it
        val weatherResponse4 = WeatherResponse(4)
     //   homeViewModel.insertWeatherDetails(weatherResponse4)
        homeViewModel.getWeatherDetails(latitude,longitude,units,lang)

        //given get the new list
        val list = repository.getWeatherDetails().first()

        //then last item is the new item
        assertThat(list.last().id, `is`(4))
    }


    @Test
    fun getWeatherDetailsList_zero_sizeThree() = runBlockingTest {
        // Call the method to be tested
        homeViewModel.getWeatherDetails(latitude, longitude, units, lang)

        // Verify the state flow value
        val apiState = homeViewModel.retrofitStateFlow.value
        assertEquals(ApiState.Success(null), apiState) // Replace `null` with the expected `WeatherResponse` value if applicable
    }

/*
    @Test
    fun getWeatherDetailsList_zero_sizeThree() = runBlockingTest {
        //when
        homeViewModel.getWeatherDetails(latitude,longitude,units,lang)
        val list = repository.getWeatherDetails().first()

        //then
        assertThat(list.size, `is`(3))
    }

 */
}

 */





