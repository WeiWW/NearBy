package com.ann.nearby

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ann.nearby.api.response.Location
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.repo.VenueRepo
import com.ann.nearby.repo.VenueRepoImpl
import com.ann.nearby.ui.main.MainViewModel
import com.ann.nearby.utils.LiveDataTestUtil
import com.ann.nearby.utils.MainCoroutineScopeRule
import com.ann.nearby.utils.SyncTaskExecutorRule
import com.ann.nearby.utils.observeForTesting
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldNotBeNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class MainViewModelTest:KoinTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()

    @get:Rule
    var coroutineRule = MainCoroutineScopeRule()

    private val mockRepo: VenueRepo = Mockito.mock(VenueRepoImpl::class.java)
    private val testModule = module {
        single { mockRepo }
    }

    private val mockLatLng = Mockito.mock(LatLng::class.java)
    private val mockLocation = Location(0, 0.0, 0.0)
    private val mockVenue = Venue("1",mockLocation)
    private val mockVenueListFlow = flow{
        emit(listOf(mockVenue))
    }

    private val mockVenueDetail = Mockito.mock(VenueDetail::class.java)
    private val mockVenueDetailFlow = flow {
        emit(mockVenueDetail)
    }

    @Before
    fun setup() {
        startKoin { modules(listOf(testModule)) }
    }

    @After
    fun cleanUp() {
        stopKoin()
        coroutineRule.coroutineContext.cancel()
        coroutineRule.cleanupTestCoroutines()
    }


    fun `MainViewModel get venue list from VenueRepo`(){
        runBlocking {
            Mockito.doReturn(mockVenueListFlow).`when`(mockRepo).getVenueList(mockLatLng)
            val viewModel = MainViewModel()
            val latLng = Mockito.mock(LatLng::class.java)
            viewModel.locationLiveData.postValue(latLng)
            viewModel.venues.observeForTesting {
                val value = LiveDataTestUtil.getValue(viewModel.venues)
                value?.get(0)?.latLng.shouldNotBeNull()
            }
        }
    }


    fun `MainViewModel query venue detail from VenueRepo`(){
        runBlocking {
            val mockLatLng = Mockito.mock(LatLng::class.java)
            Mockito.doReturn(mockVenueDetailFlow).`when`(mockRepo).getVenueDetail(mockLatLng)
            val viewModel = MainViewModel()
            viewModel.queryLiveData.postValue(mockLatLng)
            viewModel.venueDetail.observeForTesting {
                val value = LiveDataTestUtil.getValue(viewModel.venueDetail)
                value?.shouldBe(mockVenueDetail)
            }
        }
    }
}