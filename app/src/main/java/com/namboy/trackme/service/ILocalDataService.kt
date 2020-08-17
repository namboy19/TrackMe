package com.namboy.trackme.service

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.namboy.trackme.base.InlineCallback
import com.namboy.trackme.model.TrackSession

@Dao
interface ILocalDataService {

    @Query("SELECT * FROM tracksession ORDER BY uid DESC LIMIT :limit OFFSET :offset")
    suspend fun getSessionHistory(limit: Int, offset: Int): MutableList<TrackSession>

    @Insert
    suspend fun addSessionHistory(trackSession: TrackSession)
}