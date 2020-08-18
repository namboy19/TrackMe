package com.namboy.trackme.modules.record

import android.app.Application
import android.os.CountDownTimer
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.namboy.trackme.TrackMeApplication
import com.namboy.trackme.base.BaseViewModel
import com.namboy.trackme.base.InlineCallback
import com.namboy.trackme.model.TrackLocation
import com.namboy.trackme.model.TrackSession
import com.namboy.trackme.utils.AppPreferences
import com.namboy.trackme.utils.Util
import kotlinx.coroutines.launch
import java.util.*


class RecordViewModel(application: Application) : BaseViewModel(application) {

    val locationList = MutableLiveData<MutableList<TrackLocation>>()
    val distance = MutableLiveData<Float>()
    val speed = MutableLiveData<Int>()
    val time = MutableLiveData<Int>()

    init {
        distance.value = 0f
        speed.value = 0
        locationList.value= mutableListOf()
    }

    fun addTrackLocation(trackLocation: TrackLocation){
        locationList.value?.add(trackLocation)
        locationList.value=locationList.value
    }

    fun calculateDistanceAndSpeed() {
        locationList.value?.let {
            distance.value = Util.calculateDistance(it)

            if (locationList.value?.size ?: 0 > 2) {
                speed.value = Util.calculateSpeed(it.subList(it.size - 3, it.size))
            }
        }
    }

    fun saveSession(imagePath: String, callback: (Any?) -> Unit) {
        TrackMeApplication.dataManager.clearCurrentSession()

        launch {
            TrackMeApplication.dataManager.addSessionHistory(TrackSession(Gson().toJson(locationList.value) ,time.value,imagePath))
        }
        inlineCallback.value = InlineCallback(null, callback)
    }

}