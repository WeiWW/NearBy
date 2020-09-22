package com.ann.nearby.repo

import com.ann.nearby.api.ApiService
import com.ann.nearby.api.RADIUS
import com.ann.nearby.api.baseQueryMap
import com.ann.nearby.api.nearByRestaurantQueryMap
import com.ann.nearby.api.response.Location
import com.ann.nearby.api.response.Venue
import com.ann.nearby.api.response.VenueDetail
import com.ann.nearby.api.response.VenueDetailsResponse
import com.ann.nearby.utils.NetworkHelper
import com.ann.nearby.utils.getDistanceFromLanLngs
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

@ExperimentalCoroutinesApi
class VenueRepoImpl:VenueRepo,KoinComponent {
    private val apiService: ApiService by inject()
    private val networkHelper: NetworkHelper by inject()
    private val browseVenuesResults = ConflatedBroadcastChannel<List<SymbolOptions>>()
    private val venueDetailResult = ConflatedBroadcastChannel<VenueDetail?>()
    private val cacheVenuesMap:LinkedHashMap<LatLng,Venue> = linkedMapOf()
    private val hasSearchedLatLngList: MutableList<LatLng> = mutableListOf()

    @OptIn(FlowPreview::class)
    override suspend fun getVenueList(latLng: LatLng): Flow<List<SymbolOptions>> {
        val closerLatLng = getCloserLatLng(latLng)
        if (closerLatLng != null) {
            loadVenuesFromCache(closerLatLng)
        } else {
            requestVenueList(latLng)
        }
        return browseVenuesResults.asFlow()
    }
    private suspend fun requestVenueList(latLng: LatLng) {
        if (!networkHelper.isNetworkConnected()) return

        val queryMap = nearByRestaurantQueryMap(latLng)
        val response = apiService.browseNearByVenues(queryMap)
        if (!response.isSuccessful || response.body() == null) return
        response.body()?.let { venueListResponse ->
            val venueList = venueListResponse.response.venues
            addToCache(venueList)
            hasSearchedLatLngList.add(latLng)
            browseVenuesResults.offer(transformVenueToSymbol(venueList))
        }
    }

    private fun getCloserLatLng(targetLatLng: LatLng): LatLng? {
        var closerLatLng:LatLng? = null
        var minDistance = Double.MAX_VALUE

        for(latLng in hasSearchedLatLngList){
            val distance = getDistanceFromLanLngs(targetLatLng,latLng)
            if(distance < minDistance){
                minDistance = distance
                closerLatLng = latLng
            }
        }
        return if(minDistance < RADIUS/2) closerLatLng else null
    }

    private fun loadVenuesFromCache(targetLatLng: LatLng) {
        val result = mutableListOf<SymbolOptions>()
        val latLngList = cacheVenuesMap.keys //TODO: speed up
        for (latLng in latLngList) {
            if (getDistanceFromLanLngs(targetLatLng, latLng) < RADIUS) {
                val symbol = newSymbol(latLng.latitude, latLng.longitude)
                result.add(symbol)
            }
        }
        browseVenuesResults.offer(result)
    }

    private fun transformVenueToSymbol(list: List<Venue>): MutableList<SymbolOptions> {
        val result = mutableListOf<SymbolOptions>()
        for (venue in list) {
            venue.location.let { location: Location ->
                result.add(newSymbol(location.lat, location.lng))
            }
        }
        return result
    }

    private fun addToCache(list: List<Venue>){
        for (venue in list){
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
