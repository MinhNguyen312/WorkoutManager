package com.example.customapp_workoutmanager

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable

import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class ViewAndUpdateExercise : AppCompatActivity() {

    private lateinit var exerciseName: EditText
    private lateinit var exerciseDate: EditText
    private lateinit var exerciseNumOfSet: EditText
    private lateinit var exerciseNumOfRep: EditText
    private lateinit var exerciseWeightPerSet: EditText
    private lateinit var exercise: ExerciseDetail
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_and_update_exercise)


        exercise = intent.getParcelableExtra<ExerciseDetail>("Exercise")!!


        Log.i("exercise", exercise.toString())




        //  Assign Views
        exerciseName = findViewById(R.id.exerciseNameInput)
        exerciseDate = findViewById(R.id.dateInput)
        exerciseNumOfSet = findViewById(R.id.numOfSetInput)
        exerciseNumOfRep = findViewById(R.id.numOfRepInput)
        exerciseWeightPerSet = findViewById(R.id.weightInput)


            //  Assign Value to views
            exerciseName.setText(exercise.eName)
            exerciseDate.setText(exercise.eDate)
            exerciseNumOfSet.setText(exercise.eSetList.size.toString())
            exerciseNumOfRep.setText(exercise.eSetList[0].sReps.toString())
            exerciseWeightPerSet.setText(exercise.eSetList[0].sWeight.toString())






        //  Update Exercise Button
        val updateButton = findViewById<Button>(R.id.updateButton)


        updateButton.setOnClickListener {
            if(formCheck()){
                val eName = exerciseName.text.toString()
                val eDate = exerciseDate.text.toString()
                val eNumOfSet = exerciseNumOfSet.text.toString().toInt()
                val eNumOfRep = exerciseNumOfRep.text.toString().toInt()
                val eWeightPerSet = exerciseWeightPerSet.text.toString().toInt()

                //  Create a List of SetDetail
                val returnedList = createListOfSetDetail(eNumOfSet,eNumOfRep, eWeightPerSet)

                val returnIntent = Intent().apply {
                    putExtra("eID", exercise.eID)
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
        sucessText.text = "Exercise Updated Sucessfully"
        val doneBtn = dialog.findViewById<Button>(R.id.doneButton)

        dialog.show()

        doneBtn.setOnClickListener {

            dialog.dismiss()
            finish()
        }

    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
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
        val doneBtn = dialog.findViewById<Button>(R.id.cancelButton)

        doneBtn.setOnClickListener {
            dialog.dismiss()
        }

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





