package com.aswin.excerssitant.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

import com.aswin.excerssitant.R
import com.aswin.excerssitant.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var  binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val activity = this@HomeActivity as Activity
        sharedPreferences = activity.getSharedPreferences(getString(R.string.data), Context.MODE_PRIVATE)!!
        val info = sharedPreferences.getBoolean(getString(R.string.info),false)
        val reps = sharedPreferences.getInt("reps",0)
        binding.start.setOnClickListener {
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            startActivity(intent)
        }
        binding.pushupCount.text = reps.toString()

        binding.calsBurned.text = (reps * 0.32).toString()


    }

    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(intent)
    }

}