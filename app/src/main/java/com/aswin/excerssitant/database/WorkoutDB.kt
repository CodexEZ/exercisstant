package com.aswin.excerssitant.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "workout")
data class WorkoutDB(
    @PrimaryKey val id:Int,
    @ColumnInfo(name = "pushups") val push_up: Int,
    @ColumnInfo(name = "crunches") val crunches :Int,
    @ColumnInfo(name = "streak") val streak :Int,
    @ColumnInfo(name = "last_date")val date: String
)
