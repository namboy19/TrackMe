package com.namboy.trackme.utils

import android.util.Log
import com.namboy.trackme.model.TrackLocation

object Util {

    val ONE_HOUR_IN_MILIS = 3600 * 1000

    fun formatTime(second: Int): String {
        try {
            val secs = second
            return String.format("%02d:%02d:%02d", secs / 3600, secs % 3600 / 60, secs % 60)
        } catch (ex: Exception) {
            Log.d("namtest", ex.message)
            ex.printStackTrace()
            return "00:00:00"
        }
    }

    /**
     * return speed in km/ms
     */
    fun calculateSpeed(list: List<TrackLocation>, totalTime: Long? = null): Int {
        if (list.size < 2) {
            return 0
        }
        var t = if (totalTime!=null) totalTime else list.last().time - list.first().time
        var s = calculateDistance(list)

        return ((s / t)*ONE_HOUR_IN_MILIS).toInt()
    }


    /**
     * return distance in km
     */
    fun calculateDistance(list: List<TrackLocation>): Float {
        var distanceResult = 0f

        if (list.size < 2) {
            return distanceResult / 1000
        }

        var locationA = 0
        var locationB = 1

        while (locationB < list.size) {
            distanceResult += list[locationA].getLocation()
                .distanceTo(list[locationB].getLocation())
            locationA++
            locationB++
        }

        return distanceResult / 1000
    }
}