package com.ann.nearby.repo

import com.ann.nearby.api.ApiService
import com.ann.nearby.api.MIN_RADIUS
import com.ann.nearby.api.baseQueryMap
import com.ann.nearby.api.nearByRestaurantQueryMap
import com.ann.nearby.api.request.VenueRequest
import com.ann.nearby.api.response.Location
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.api.response.VenueDetailsResponse
import com.ann.nearby.utils.NetworkHelper
import com.ann.nearby.utils.newSymbol
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.collections.set

@ExperimentalCoroutinesApi
class VenueRepoImpl:VenueRepo,KoinComponent {
    private val apiService: ApiService by inject()
    private val networkHelper: NetworkHelper by inject()
    private val browseVenuesResults = ConflatedBroadcastChannel<List<SymbolOptions>>()
    private val venueDetailResult = ConflatedBroadcastChannel<VenueDetail?>()
    private val cacheVenuesMap:LinkedHashMap<LatLng,Venue> = linkedMapOf()

    @OptIn(FlowPreview::class)
    override suspend fun getVenueList(venueRequest: VenueRequest): Flow<List<SymbolOptions>> {
        loadVenuesFromCache(venueRequest.latLng, venueRequest.radius)
        requestVenueList(venueRequest)
        return browseVenuesResults.asFlow()
    }

    private suspend fun requestVenueList(venueRequest: VenueRequest) {
        if (!networkHelper.isNetworkConnected()) return

        // If radius smaller than 500, not to request
        if (venueRequest.radius < MIN_RADIUS) return
        val queryMap = nearByRestaurantQueryMap(venueRequest.latLng, venueRequest.radius)
        val response = apiService.browseNearByVenues(queryMap)
        if (!response.isSuccessful || response.body() == null) return

        response.body()?.let { venueListResponse ->
            val venueList = venueListResponse.response.venues
            addToCache(venueList)
            loadVenuesFromCache(venueRequest.latLng, venueRequest.radius)
        }
    }

    /*
    * If the distance, between the cache venue and target position,
    * is smaller than the radius, add it into result list and send them back.
    * */
    private fun loadVenuesFromCache(targetLatLng: LatLng, radius: Double) {
        val result = mutableListOf<SymbolOptions>()
        val latLngList = cacheVenuesMap.keys
        //remove those not in radius
        latLngList.removeIf {
            it.distanceTo(targetLatLng) > radius
        }

        for (latLng in latLngList) {
            val symbol = newSymbol(latLng.latitude, latLng.longitude)
            result.add(symbol)
        }

        browseVenuesResults.offer(result)
    }

    private fun addToCache(list: List<Venue>){
        for (venue in list){
            //Use the venue's location as the key of the map.
            val key = venue.location.let{location: Location ->
                LatLng(location.lat,location.lng)
            }
            if (!cacheVenuesMap.containsKey(key)){
                cacheVenuesMap[key] = venue
            }
        }
    }

    @OptIn(FlowPreview::class)
    override suspend fun getVenueDetail(latLng: LatLng): Flow<VenueDetail?> {
        //The purpose is getting venue's id
        val venue = cacheVenuesMap[latLng]
        venue?.let {
            requestVenueDetail(it)
        }
        return venueDetailResult.asFlow()
    }

    private suspend fun requestVenueDetail(venue: Venue){
        if (!networkHelper.isNetworkConnected()) return

        val response = apiService.getVenueDetail(venue.id, baseQueryMap)
        if (!response.isSuccessful || response.body() == null) return
        response.body()?.let { venueDetailResponse: VenueDetailsResponse ->
            val detail = venueDetailResponse.response.venue
            venueDetailResult.offer(detail)
        }
    }

}
