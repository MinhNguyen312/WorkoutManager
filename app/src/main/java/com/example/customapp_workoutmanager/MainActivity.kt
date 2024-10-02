package com.example.customapp_workoutmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.customapp_workoutmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Initial Fragment
        replaceFragment(WorkoutHistory())
        binding.bottomNavigationView.selectedItemId = R.id.history


        //  Navigation Implementation
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.exercises -> replaceFragment(Exercises())
                R.id.history -> replaceFragment(WorkoutHistory())
                R.id.profile -> replaceFragment(Profile())
                else -> {

                }
            }

            true
        }

    }

    //  Method to replace FrameLayout with Fragments

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}