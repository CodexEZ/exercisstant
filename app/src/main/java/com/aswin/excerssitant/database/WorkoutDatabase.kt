package com.aswin.excerssitant.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities =[WorkoutDB::class] , version = 1)
abstract class WorkoutDatabase:RoomDatabase() {
    abstract fun workoutDAO():WorkoutDAO
}