package com.namboy.trackme.service

import com.google.gson.Gson
import com.namboy.trackme.TrackMeApplication
import com.namboy.trackme.model.TrackSession
import com.namboy.trackme.utils.AppPreferences
import kotlinx.coroutines.runBlocking

class LocalDataService(val appPreferences: AppPreferences, val gson: Gson) : ILocalDataService {

    override suspend fun getSessionHistory(limit: Int, offset: Int): MutableList<TrackSession> {
         appPreferences.getString(AppPreferences.PREFS_TRACK_HISTORY).takeIf { it.isNotEmpty() }
            ?.also {
                return gson.fromJson(it, Array<TrackSession>::class.java).toMutableList()
            }
        return mutableListOf()
    }

    override suspend fun addSessionHistory(trackSession: TrackSession) {
        var list= getSessionHistory(0,0)
        list.add(0,trackSession)
        appPreferences.saveString(AppPreferences.PREFS_TRACK_HISTORY, gson.toJson(list))
    }
}
