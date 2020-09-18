package com.ann.nearby

import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ann.nearby.api.response.Location
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.repo.VenueRepo
import com.ann.nearby.repo.VenueRepoImpl
import com.ann.nearby.ui.main.MainViewModel
import com.ann.nearby.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.should
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mock
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

    private val mockLocation = Location(0, 0.0, 0.0)
    private val mockVenue = Venue(emptyList(), "1", mockLocation, "mock")
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

    @Test
    fun `MainViewModel get venue list from VenueRepo`(){
        runBlocking {
            Mockito.doReturn(mockVenueListFlow).`when`(mockRepo).getVenueList(emptyMap())
            val viewModel = MainViewModel()
            val location = Mockito.mock(android.location.Location::class.java)
            viewModel.locationLiveData.value = location
            viewModel.venues.observeForTesting {
                val value = LiveDataTestUtil.getValue(viewModel.venues)
                value?.get(0)?.id.shouldBeEqualTo(mockVenue.id)
            }
        }
    }

    @Test
    fun `MainViewModel query venue detail from VenueRepo`(){
        runBlocking {
            Mockito.doReturn(mockVenueDetailFlow).`when`(mockRepo).getVenueDetail("", emptyMap())
            val viewModel = MainViewModel()
            viewModel.queryLiveData.postValue("cafe")
            viewModel.venueDetail.observeForTesting {
                val value = LiveDataTestUtil.getValue(viewModel.venueDetail)
                value?.shouldBe(mockVenueDetail)
            }
        }
    }
}