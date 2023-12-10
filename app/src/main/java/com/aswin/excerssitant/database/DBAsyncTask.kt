package com.aswin.excerssitant.database

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room

class DBAsyncTask(val context: Context, val workout:WorkoutDB, val mode:String):AsyncTask<Void,Void,Boolean>() {
    override fun doInBackground(vararg p0: Void?): Boolean {
        val db = Room.databaseBuilder(context,WorkoutDatabase::class.java,"workout-db").build()
        when (mode){
            "upsert"->{
                db.workoutDAO().upsert(workout)
                db.close()
                return true
            }
            "delete"->{
                db.workoutDAO().delete(workout)
                db.close()
                return true
            }
            "read"->{
                val workout = db.workoutDAO().getStatsById(workout.id)
                db.close()
                return workout!=null
            }
        }
        return false
    }

}

class getStats(val context:Context, val id:Int):AsyncTask<Void,Void,WorkoutDB>(){
    override fun doInBackground(vararg p0: Void?): WorkoutDB {
        val db = Room.databaseBuilder(context,WorkoutDatabase::class.java,"workout-db").build()
        val workout:WorkoutDB = db.workoutDAO().getStatsById(id)
        db.close()
        return workout
    }

}