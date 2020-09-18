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
import com.ann.nearby.api.response.Venue
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldNotBe

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
    private val queryMap = nearByRestaurantQueryMap("25.0034405", "121.5369503", "500")

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
            val liveData = repo.getVenueList(queryMap).asLiveData()
            liveData.observeForever { result: List<Venue> ->
                result.size.shouldBeGreaterThan(0)
            }
        }

        yield()
    }
}