package com.namboy.trackme

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.namboy.trackme.service.DataManager
//import com.namboy.trackme.service.LocalDataService
import com.namboy.trackme.service.RoomDataService
import com.namboy.trackme.utils.AppPreferences

class TrackMeApplication : Application() {

    companion object {
        lateinit var appPreferences: AppPreferences
        lateinit var dataManager: DataManager
        lateinit var appDatabaseService: RoomDataService
        var gson = Gson()
    }

    override fun onCreate() {
        super.onCreate()

        appPreferences = AppPreferences(this)
        //localDataService= LocalDataService(appPreferences, Gson())
        appDatabaseService =
            Room.databaseBuilder(applicationContext, RoomDataService::class.java, "database-name")
                .build()

        dataManager = DataManager(appDatabaseService.trackingDAO(), appPreferences, gson)
    }

}