package com.aswin.excerssitant.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface WorkoutDAO {
    @Upsert
    fun upsert(workout:WorkoutDB)

    @Delete
    fun delete(workout: WorkoutDB)

    @Query("SELECT * FROM workout WHERE id = :id")
    fun getStatsById(id:Int):WorkoutDB
}