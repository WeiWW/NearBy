package com.ann.nearby.ui.main

import android.location.Location
import androidx.lifecycle.*
import com.ann.nearby.api.baseQueryMap
import com.ann.nearby.api.nearByRestaurantQueryMap
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.repo.VenueRepo
import com.mapbox.geojson.Feature
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainViewModel : ViewModel(),KoinComponent {
    private val repo:VenueRepo by inject()

    val locationLiveData = MutableLiveData<Location>()
    val venues:LiveData<List<Feature>> = locationLiveData.switchMap { location:Location ->
        liveData {
            val queryMap
                    = nearByRestaurantQueryMap(location.latitude.toString(),location.longitude.toString(),"500")
            val data = repo.getVenueList(queryMap).asLiveData()
            emitSource(data)
        }
    }

    val queryLiveData = MutableLiveData<String>()
    val venueDetail:LiveData<VenueDetail?> = queryLiveData.switchMap { venueId:String ->
        liveData {
            val queryMap = baseQueryMap
            val data = repo.getVenueDetail(venueId,queryMap).asLiveData()
            emitSource(data)
        }
    }
}