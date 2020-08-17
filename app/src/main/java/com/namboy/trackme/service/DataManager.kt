package com.namboy.trackme.service

import com.google.gson.Gson
import com.namboy.trackme.model.TrackSession
import com.namboy.trackme.utils.AppPreferences

class DataManager(val localDataService: ILocalDataService, val appPreferences: AppPreferences, val gson: Gson):ILocalDataService {

    fun clearCurrentSession() {
        appPreferences.saveString(AppPreferences.PREFS_CURRENT_SESSION, "")
    }

    fun getCurrentSession():TrackSession? {
        appPreferences.getString(AppPreferences.PREFS_CURRENT_SESSION)
            .takeIf { it.isNotEmpty() }?.also {
                return gson.fromJson(it, TrackSession::class.java)
            }
        return null
    }

    override suspend fun addSessionHistory(trackSession: TrackSession) {
        /*for (i in 0..100){
            localDataService.addSessionHistory(trackSession)
        }
*/
        localDataService.addSessionHistory(trackSession)
    }

    override suspend fun getSessionHistory(limit: Int, offset: Int) : MutableList<TrackSession> {
        return localDataService.getSessionHistory(limit, offset)
    }

}