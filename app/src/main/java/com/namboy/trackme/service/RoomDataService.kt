package com.namboy.trackme.service

import androidx.room.Database
import androidx.room.RoomDatabase
import com.namboy.trackme.model.TrackSession

@Database(entities = arrayOf(TrackSession::class), version = 1)
abstract class RoomDataService : RoomDatabase() {
    abstract fun trackingDAO(): ILocalDataService
}