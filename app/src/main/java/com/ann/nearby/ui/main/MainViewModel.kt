package com.ann.nearby.ui.main

import android.location.Location
import androidx.lifecycle.*
import com.ann.nearby.api.baseQueryMap
import com.ann.nearby.api.nearByRestaurantQueryMap
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.repo.VenueRepo
import com.mapbox.mapboxsdk.geometry.LatLng
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainViewModel : ViewModel(),KoinComponent {
    private val repo:VenueRepo by inject()

    val locationLiveData = MutableLiveData<Location>()
    val venues:LiveData<List<Venue>> = locationLiveData.switchMap { location:Location ->
        liveData {
            val queryMap
                    = nearByRestaurantQueryMap(location,"500")
            val data = repo.getVenueList(queryMap).asLiveData()
            emitSource(data)
        }
    }

    val queryLiveData = MutableLiveData<LatLng>()
    val venueDetail:LiveData<VenueDetail?> = queryLiveData.switchMap { latlng:LatLng ->
        liveData {
            val queryMap = baseQueryMap
            val data = repo.getVenueDetail(latlng,queryMap).asLiveData()
            emitSource(data)
        }
    }
}