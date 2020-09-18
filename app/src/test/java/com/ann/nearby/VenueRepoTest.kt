package com.ann.nearby

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ann.nearby.api.nearByRestaurantQueryMap
import com.ann.nearby.di.module.venueRepoModule
import com.ann.nearby.repo.VenueRepo
import com.ann.nearby.utils.MainCoroutineScopeRule
import com.ann.nearby.utils.MockSeverBase
import com.ann.nearby.utils.SyncTaskExecutorRule
import com.ann.nearby.utils.networkTestModule
import kotlinx.coroutines.*
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
import java.net.HttpURLConnection
import androidx.lifecycle.asLiveData
import com.ann.nearby.api.baseQueryMap
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import org.amshove.kluent.*

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class VenueRepoTest:MockSeverBase(),KoinTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()


    @get:Rule
    var coroutineRule = MainCoroutineScopeRule()

    private val repo: VenueRepo by inject()
    private val MOCK_QUERY_VENUES_MAP = nearByRestaurantQueryMap("25.0034405", "121.5369503", "500")
    private val MOCK_QUERY_VENUE_DETAIL_MAP = baseQueryMap
    private val MOCK_VENUE_ID = "4c5ef77bfff99c74eda954d3"

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
    fun `browse nearby restaurants success`() = runBlocking {
        enqueue(
            `mock network response with json file`(
                HttpURLConnection.HTTP_OK,
                "browse_venues_success.json"
            )
        )

        launch(Dispatchers.Default) {
            val liveData = repo.getVenueList(MOCK_QUERY_VENUES_MAP).asLiveData()
            liveData.observeForever { result: List<Venue> ->
                result.size.shouldBeGreaterThan(0)
            }
        }

        yield()
    }

    @Test
    fun `browse nearby restaurants, get empty list`() = runBlocking {
        enqueue(
            `mock network response with json file`(
                HttpURLConnection.HTTP_OK,
                "browse_venues_empty.json"
            )
        )

        launch(Dispatchers.Default) {
            val liveData = repo.getVenueList(MOCK_QUERY_VENUES_MAP).asLiveData()
            liveData.observeForever{result:List<Venue> ->
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
            val liveData = repo.getVenueList(MOCK_QUERY_VENUES_MAP).asLiveData()
            liveData.observeForever{result:List<Venue> ->
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
            val liveData = repo.getVenueDetail(MOCK_VENUE_ID,MOCK_QUERY_VENUE_DETAIL_MAP).asLiveData()
            liveData.observeForever{detail:VenueDetail ->
                detail.id.shouldBeEqualTo(MOCK_VENUE_ID)
            }
        }

        yield()
    }
}