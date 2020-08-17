package com.namboy.trackme.model

import android.location.Location
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrackLocation(val lat: Double, val lng: Double, val time: Long) : Parcelable {

    fun getLocation(): Location {
        return Location(time.toString()).apply {
            this.latitude = lat
            this.longitude = lng
        }
    }

    fun getLatLng(): LatLng {
        return LatLng(lat, lng)
    }
}