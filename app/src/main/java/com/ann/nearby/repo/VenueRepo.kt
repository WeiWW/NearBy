package com.ann.nearby.repo

interface VenueRepo {
    fun getVenueList(filter: Map<String, String>)
    fun getVenueDetail(venueId: String)
}