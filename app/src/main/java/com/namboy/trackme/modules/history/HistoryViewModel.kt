package com.namboy.trackme.modules.history

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.namboy.trackme.TrackMeApplication
import com.namboy.trackme.base.BaseViewModel
import com.namboy.trackme.model.TrackSession
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : BaseViewModel(application) {

    val trackSessionLiveData by lazy { MutableLiveData<Pair<Boolean,MutableList<TrackSession>>> () }
    val trackSessionList = mutableListOf<TrackSession>()

    private val limit = 10

    fun loadData() {
        trackSessionList.clear()
        launch {
            var result = TrackMeApplication.dataManager.getSessionHistory(limit, 0)
            trackSessionLiveData.postValue(Pair(true,result))
            trackSessionList.addAll(result)
        }
    }

    fun loadMore(offset: Int) {
        launch {
            var result = TrackMeApplication.dataManager.getSessionHistory(limit, offset)
            trackSessionLiveData.postValue(Pair(false,result))
            trackSessionList.addAll(result)
        }

    }

}