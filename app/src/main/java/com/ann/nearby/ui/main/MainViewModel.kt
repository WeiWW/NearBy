package com.ann.nearby.ui.main

import androidx.lifecycle.*
import com.ann.nearby.api.request.VenueRequest
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.repo.VenueRepo
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainViewModel : ViewModel(),KoinComponent {
    private val repo:VenueRepo by inject()

    val locationLiveData = MutableLiveData<VenueRequest>()
    val venues: LiveData<List<SymbolOptions>> = locationLiveData.switchMap { venueRequest: VenueRequest ->
        liveData {
            val data = repo.getVenueList(venueRequest).asLiveData()
            emitSource(data)
        }
    }

    val queryLiveData = MutableLiveData<LatLng>()
    val venueDetail:LiveData<VenueDetail?> = queryLiveData.switchMap { latlng:LatLng ->
        liveData {
            val data = repo.getVenueDetail(latlng).asLiveData()
            emitSource(data)
        }
    }
}