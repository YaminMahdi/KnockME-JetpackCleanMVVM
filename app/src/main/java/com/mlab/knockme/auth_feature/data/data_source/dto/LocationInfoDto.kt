package com.mlab.knockme.auth_feature.data.data_source.dto

import com.mlab.knockme.auth_feature.domain.model.LocationInfo

data class LocationInfoDto(
    val city: String,
    val country: String,
    val countryCode: String,
    val district: String,
    val query: String,
    val regionName: String,
    val status: String
){
    fun toLocationInfo()=
        LocationInfo(
            ip = query,
            loc = "$city, $country"
        )
}