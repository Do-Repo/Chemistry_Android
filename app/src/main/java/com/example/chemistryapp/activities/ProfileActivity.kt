package com.example.chemistryapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.chemistryapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)
        replaceFragment(HomeFragment())

        findViewById<BottomNavigationView>(R.id.bottomNavigationView).setOnItemSelectedListener{ id ->
            when(id.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.settings -> replaceFragment(SettingsFragment())
                else -> {

                }
            }
            true

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        var fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}