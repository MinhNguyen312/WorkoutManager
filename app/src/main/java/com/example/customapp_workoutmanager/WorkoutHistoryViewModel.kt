package com.example.customapp_workoutmanager

import android.app.Application
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WorkoutHistoryViewModel : ViewModel() {



    //  List of Exercises
    private val _listOfExercises = MutableLiveData<MutableList<ExerciseDetail>>()
    val listOfExercises: LiveData<MutableList<ExerciseDetail>>
        get() = _listOfExercises
    init {
        fetchWorkoutDetails()
    }

    private val mockListOfExercises = mutableListOf<ExerciseDetail>()

    private fun fetchWorkoutDetails(){

        viewModelScope.launch {
            delay(10)

            readData()
            Log.i("Data Retrieve", "Sucessfully")
            _listOfExercises.postValue(mockListOfExercises)

            Log.i("Data", _listOfExercises.value.toString())
        }

    }

    fun addExercise(newExercise: ExerciseDetail){
        mockListOfExercises.add(newExercise)

        _listOfExercises.postValue(mockListOfExercises)
    }

    fun updateExercise(newExercise: ExerciseDetail){
        mockListOfExercises[newExercise.eID - 1] = newExercise

        _listOfExercises.postValue(mockListOfExercises)
    }

    private fun populateTempListOfExercises(eID: Int, eDate:String, eName:String, eSetList: List<SetDetail>){

        val data = ExerciseDetail(eID,eDate, eName, eSetList)
        Log.i("Populate Data", data.toString())

        addExercise(data)
    }


    private fun readData(){
        val database = Firebase.database("https://workoutmanager-99900-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("104225962")

        myRef.get().addOnSuccessListener {
            if(it.exists()){
               it.children.forEach{ dataSnapshot ->

                   dataSnapshot.children.forEach{ exerciseSnapshot ->
                       val eID = exerciseSnapshot.child("eid").getValue()
                       val eName = exerciseSnapshot.child("ename").getValue(String::class.java)
                       val eDate = exerciseSnapshot.child("edate").getValue(String::class.java)
                       val setList = mutableListOf<SetDetail>()

                       exerciseSnapshot.child("esetList").children.forEach{setSnapshot ->
                           val sReps = setSnapshot.child("sreps").getValue(Int::class.java)
                           val sWeight = setSnapshot.child("sweight").getValue(Int::class.java)
                           val stability = setSnapshot.child("stability").getValue(Int::class.java)
                           val setDetail = SetDetail(sReps!!, sWeight!!)
                           setList.add(setDetail)
                       }

                       populateTempListOfExercises(eID.toString().toInt(),eDate.toString(),eName.toString(),setList)
                   }
               }


            }
        }
            .addOnFailureListener {
                Log.i("Data Retrieve", "Failed")
            }

    }




}