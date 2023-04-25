package com.example.chemistryapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("my_app_pref", Context.MODE_PRIVATE)


        Timer().schedule(3000){
            val isLoggedIn = sharedPref.getBoolean("is_logged_in", false)

            if(isLoggedIn){
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
            } else {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            finish()
        }
    }
}