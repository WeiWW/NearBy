package com.ann.nearby

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.repo.VenueRepo
import com.ann.nearby.repo.VenueRepoImpl
import com.ann.nearby.ui.main.MainViewModel
import com.ann.nearby.utils.MainCoroutineScopeRule
import com.ann.nearby.utils.SyncTaskExecutorRule
import com.ann.nearby.utils.newSymbol
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.mock
import org.amshove.kluent.shouldBe
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.ArgumentCaptor
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
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        startKoin { modules(listOf(testModule)) }
        viewModel = MainViewModel()
    }

    @After
    fun cleanUp() {
        stopKoin()
        coroutineRule.coroutineContext.cancel()
        coroutineRule.cleanupTestCoroutines()
    }


    @Test
    fun `MainViewModel get venue list from VenueRepo successfully`() = coroutineRule.dispatcher.runBlockingTest {
        //Given
        val observer:Observer<List<SymbolOptions>> = mock()
        viewModel.venues.observeForever(observer)

        val latLng = LatLng(25.0034405, 121.5369503)
        val expectSymbol = newSymbol(latLng.latitude, latLng.longitude)

        val venuesChannel = Channel<List<SymbolOptions>>()
        val venuesFlow = venuesChannel.consumeAsFlow()

        //When
        Mockito.`when`(mockRepo.getVenueList(latLng)).thenReturn(venuesFlow)

        //Then
        viewModel.locationLiveData.postValue(latLng)
        launch {
            venuesChannel.send(listOf(expectSymbol))
        }

        val captor:ArgumentCaptor<List<SymbolOptions>> = ArgumentCaptor.forClass(List::class.java as Class<List<SymbolOptions>>)
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            this.value[0].shouldBe(expectSymbol)
        }
    }

    @Test
    fun `MainViewModel get venue detail from repo successfully`() = coroutineRule.dispatcher.runBlockingTest {
        //Given
        val observer:Observer<VenueDetail?> = mock()
        viewModel.venueDetail.observeForever(observer)
        val latLng = LatLng(25.0034405, 121.5369503)

        val venueDetailChannel = Channel<VenueDetail>()
        val venueDetailFlow = venueDetailChannel.consumeAsFlow()
        val expectVenueDetail = VenueDetail(null,null,null,"0","expect",0.0)

        //When
        Mockito.`when`(mockRepo.getVenueDetail(latLng)).thenReturn(venueDetailFlow)
        //Then
        viewModel.queryLiveData.postValue(latLng)
        launch {
            venueDetailChannel.send(expectVenueDetail)
        }

        val captor = ArgumentCaptor.forClass(VenueDetail::class.java)
        captor.run {
            Mockito.verify(observer).onChanged(capture())
            this.value.shouldBe(expectVenueDetail)
        }
    }

}