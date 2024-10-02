package com.example.customapp_workoutmanager

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.database.database

class AddExercise : AppCompatActivity() {

    private lateinit var exerciseName:EditText
    private lateinit var exerciseDate:EditText
    private lateinit var exerciseNumOfSet:EditText
    private lateinit var exerciseNumOfRep:EditText
    private lateinit var exerciseWeightPerSet:EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)


        //  Retrieve User Input Data
        exerciseName = findViewById(R.id.exerciseNameInput)
        exerciseDate = findViewById(R.id.dateInput)
        exerciseNumOfSet = findViewById(R.id.numOfSetInput)
        exerciseNumOfRep = findViewById(R.id.numOfRepInput)
        exerciseWeightPerSet = findViewById(R.id.weightInput)

        //  Add Exercise Button
        val addButton = findViewById<Button>(R.id.addButton)


        //  Set Button onClickListener
        addButton.setOnClickListener {
            Log.i("formValidation", formCheck().toString())

            if(formCheck()){
                //  Input Data
                val eName = exerciseName.text.toString()
                val eDate = exerciseDate.text.toString()
                val eNumOfSet = exerciseNumOfSet.text.toString().toInt()
                val eNumOfRep = exerciseNumOfRep.text.toString().toInt()
                val eWeightPerSet = exerciseWeightPerSet.text.toString().toInt()

                //  Create a List of SetDetail
                val returnedList = createListOfSetDetail(eNumOfSet,eNumOfRep, eWeightPerSet)

                val returnIntent = Intent().apply {
                    putExtra("eID", intent.getIntExtra("numOfSession",0) + 1)
                    putExtra("eDate", eDate)
                    putExtra("eName", eName)
                    putParcelableArrayListExtra("ListOfSetDetails", ArrayList(returnedList))
                }



                setResult(Activity.RESULT_OK,returnIntent)

                showDialog()
            }
        }
    }

    private fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.success_dialog)


        val window = dialog.window
        val layoutParams = window?.attributes

        layoutParams?.gravity = Gravity.CENTER
        window?.attributes = layoutParams


        val sucessText = dialog.findViewById<TextView>(R.id.sucessText)
        sucessText.text = "Exercise Added Sucessfully"
        val doneBtn = dialog.findViewById<Button>(R.id.doneButton)

        dialog.show()

        doneBtn.setOnClickListener {

            dialog.dismiss()
            finish()
        }

    }

    private fun showError(error: String){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.error_dialog)


        val window = dialog.window
        val layoutParams = window?.attributes

        layoutParams?.gravity = Gravity.CENTER
        window?.attributes = layoutParams

        val errorText = dialog.findViewById<TextView>(R.id.errorText)

        errorText.text = error
        val cancelBtn = dialog.findViewById<Button>(R.id.cancelButton)

        dialog.show()

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun formCheck() :Boolean {
        val eName = exerciseName.text.toString().trim()
        val eDate = exerciseDate.text.toString().trim()
        val numOfSet = exerciseNumOfSet.text.toString().trim()
        val numOfRep = exerciseNumOfRep.text.toString().trim()
        val weightPerSet = exerciseWeightPerSet.text.toString().trim()

        Log.i("test",eName.isBlank().toString())
        if(eName.isEmpty()){
            showError("Name is required")
            return false
        }

        if(eDate.isBlank()){
            showError("Date is required")
            return false
        }


        if(numOfSet.isBlank()){
            showError("Number of Set is required")
            return false
        }


        if(numOfRep.isBlank()){
            showError("Number of Rep is required")
            return false
        }


        if(weightPerSet.isBlank()){
            showError("Weight (Kg) is required")
            return false
        }
        return true
    }

    private fun createListOfSetDetail(numOfSet: Int, numOfRep: Int,weightPerSet: Int) : List<SetDetail> {
        val result = mutableListOf<SetDetail>()

        for(i in 1..numOfSet){
            result.add(SetDetail(numOfRep,weightPerSet))
        }

        return result
    }


}