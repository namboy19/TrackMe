package com.namboy.trackme.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class TrackSession(
    @ColumnInfo(name = "trackLocationList") val trackLocationList: String? = null,
    @ColumnInfo(name = "timeSecond") val timeSecond: Int? = null,
    @ColumnInfo(name = "imagePath") val imagePath: String? = null,
    @PrimaryKey(autoGenerate = true) val uid: Long=0
) : Parcelable {

    fun getTimeInLong(): Long {
        return (timeSecond?.toLong()?:0) * 1000
    }

    fun getTrackLocationList(gson:Gson):MutableList<TrackLocation>{
        return try {
            gson.fromJson(trackLocationList, Array<TrackLocation>::class.java).toMutableList()
        }
        catch (ex:Exception){
            ex.printStackTrace()
            mutableListOf<TrackLocation>()
        }
    }
}