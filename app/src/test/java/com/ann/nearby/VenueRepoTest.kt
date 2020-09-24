package com.ann.nearby

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.ann.nearby.api.request.VenueRequest
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.di.module.venueRepoModule
import com.ann.nearby.repo.VenueRepo
import com.ann.nearby.utils.TestCoroutineScopeRule
import com.ann.nearby.utils.MockSeverBase
import com.ann.nearby.utils.SyncTaskExecutorRule
import com.ann.nearby.utils.networkTestModule
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import kotlinx.coroutines.*
import org.amshove.kluent.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito
import java.net.HttpURLConnection
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class VenueRepoTest:MockSeverBase(),KoinTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()


    @get:Rule
    var coroutineRule = TestCoroutineScopeRule()

    private val repo: VenueRepo by inject()
    private val mockLatLng = Mockito.mock(LatLng::class.java)
    private val mockRequest:VenueRequest = Mockito.mock(VenueRequest::class.java)

    @Before
    override fun setup() {
        super.setup()
        startKoin { modules(listOf(venueRepoModule, networkTestModule(getUrl()))) }
    }

    @After
    override fun tearDown() {
        super.tearDown()
        stopKoin()
        coroutineRule.coroutineContext.cancel()
        coroutineRule.cleanupTestCoroutines()
    }

    @Test
    fun `search nearby restaurants success`() = runBlocking {
        enqueue(
            `mock network response with json file`(
                HttpURLConnection.HTTP_OK,
                "browse_venues_success.json"
            )
        )

        launch(Dispatchers.Default) {
            val liveData = repo.getVenueList(mockRequest).asLiveData()
            liveData.observeForever { result: List<SymbolOptions> ->
                result.size.shouldBeEqualTo(0)
            }
        }

        yield()
    }

    @Test
    fun `search nearby restaurants, get empty list`() = runBlocking {
        enqueue(
            `mock network response with json file`(
                HttpURLConnection.HTTP_OK,
                "browse_venues_empty.json"
            )
        )

        launch(Dispatchers.Default) {
            val liveData = repo.getVenueList(mockRequest).asLiveData()
            liveData.observeForever{result:List<SymbolOptions> ->
                result.size.shouldBeEqualTo(0)
            }
        }

        yield()
    }

    @Test
    fun `browse nearby restaurants,response error, http 400`() = runBlocking {
        enqueue(
            `mock network response with json file`(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "browse_venues_error_400.json"
            )
        )

        launch(Dispatchers.Default) {
            val liveData = repo.getVenueList(mockRequest).asLiveData()
            liveData.observeForever{result:List<SymbolOptions> ->
                result.shouldBeEmpty()
            }
        }

        yield()
    }

    @Test
    fun `get venue detail successfully`() = runBlocking {
        enqueue(
            `mock network response with json file`(
                HttpURLConnection.HTTP_OK,
                "venue_detail_success.json"
            )
        )

        launch(Dispatchers.Default) {
            val liveData = repo.getVenueDetail(mockLatLng).asLiveData()
            liveData.observeForever{detail:VenueDetail? ->
                detail?.id?.shouldNotBeNullOrBlank()
            }
        }

        yield()
    }

    @Test
    fun `get venue detail error`() = runBlocking {
        enqueue(
            `mock network response with json file`(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "venue_detail_error_400.json"
            )
        )

        launch(Dispatchers.Default) {
            val liveData = repo.getVenueDetail(mockLatLng).asLiveData()
            liveData.observeForever{detail:VenueDetail? ->
                detail?.shouldBeNull()
            }
        }

        yield()
    }
}