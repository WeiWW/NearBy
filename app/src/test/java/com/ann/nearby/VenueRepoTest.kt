package com.ann.nearby

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ann.nearby.di.module.venueRepoModule
import com.ann.nearby.utils.MainCoroutineScopeRule
import com.ann.nearby.utils.MockSeverBase
import com.ann.nearby.utils.SyncTaskExecutorRule
import com.ann.nearby.utils.networkTestModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class VenueRepoTest:MockSeverBase(),KoinTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    var syncTaskExecutorRule = SyncTaskExecutorRule()


    @get:Rule
    var coroutineRule = MainCoroutineScopeRule()

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
}