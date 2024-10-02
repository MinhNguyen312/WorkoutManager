package com.example.customapp_workoutmanager

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.parcelize.Parcelize

class WorkoutHistory : Fragment() {

    private lateinit var exerciseList: RecyclerView
    private lateinit var data: MutableList<ExerciseDetail>
    private var adapter = WorkoutHistoryAdapter()

    companion object {
        fun newInstance() = WorkoutHistory()
    }

    private lateinit var viewModel: WorkoutHistoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_workout_history, container, false)
        val fabButton = view.findViewById<FloatingActionButton>(R.id.sessionFab)

        exerciseList = view.findViewById(R.id.exercisesList)
        registerForContextMenu(exerciseList)

        fabButton.setOnClickListener{
            val params = viewModel.listOfExercises.value?.let { it1 ->
                ExerciseContract.Params("104225962",
                    it1.size)
            }

            addExercise.launch(params)
        }


        exerciseList.adapter = adapter
        exerciseList.layoutManager = LinearLayoutManager(this.context)

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WorkoutHistoryViewModel::class.java)

        viewModel.listOfExercises.observe(viewLifecycleOwner){listOfExercises ->
            adapter.submitList(listOfExercises)

        }

        if(viewModel.listOfExercises.value?.isNotEmpty() == true){
            adapter.submitList(viewModel.listOfExercises.value!!)
        }

    }

    //  On Context Item Select (View and Delete)
    override fun onContextItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.viewExercise -> {

                viewExercise.launch(viewModel.listOfExercises.value?.get(item.groupId))

                true
            }

            R.id.deleteExercise -> {
                showSuccessDeleteDialog()
                adapter.removeExercise(item.groupId)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    //  Show Success delete message
    private fun showSuccessDeleteDialog(){
        val dialog = Dialog(this.requireContext())
        dialog.setContentView(R.layout.success_dialog)

        val window = dialog.window
        val layoutParams = window?.attributes

        layoutParams?.gravity = Gravity.CENTER
        window?.attributes = layoutParams

        val sucessText = dialog.findViewById<TextView>(R.id.sucessText)

        sucessText.text = "Exercise Deleted Sucessfully!"
        val doneBtn = dialog.findViewById<Button>(R.id.doneButton)

        dialog.show()

        doneBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    //  Create Option Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.context_menu_layout, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun createOnDatabase(sDate: String, exerciseDetail: ExerciseDetail){
        // Write a message to the database
        val database = Firebase.database("https://workoutmanager-99900-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("104225962")

        myRef.child(sDate).child(exerciseDetail.eID.toString()).setValue(exerciseDetail)
    }

    //  View and Edit Exercise
    private val viewExercise = registerForActivityResult(ViewExerciseContract(), ){result ->
        if(result.eID != -1){
            viewModel.updateExercise(result)
            adapter.updateList(result)
            adapter.submitList(viewModel.listOfExercises.value!!)
        }
    }

    class ViewExerciseContract:ActivityResultContract<ExerciseDetail, ExerciseDetail>(){
        override fun createIntent(context: Context, input: ExerciseDetail): Intent {
            return Intent(context, ViewAndUpdateExercise::class.java).apply {
                putExtra("Exercise", input)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ExerciseDetail {
            return if(resultCode == RESULT_OK && intent != null){
                val newExerciseID = intent.getIntExtra("eID", 0)
                val newExerciseDate = intent.getStringExtra("eDate")
                val newExerciseName = intent.getStringExtra("eName")
                val newExerciseParcelable = intent.getParcelableArrayListExtra<Parcelable>("ListOfSetDetails")
                val newExerciseList = newExerciseParcelable?.mapNotNull { it as SetDetail } ?: emptyList()

                ExerciseDetail(newExerciseID,newExerciseDate!!,newExerciseName!!, newExerciseList)
            } else {
                ExerciseDetail(-1,"","", arrayListOf())
            }
        }

    }


    //  Add Exercise Activity

    private val addExercise = registerForActivityResult(ExerciseContract(),) {result ->
        if(result.eID != -1){
            createOnDatabase(result.eDate, result)
            viewModel.addExercise(result)
            adapter.submitList(viewModel.listOfExercises.value!!)
        }
    }

    class ExerciseContract:ActivityResultContract<ExerciseContract.Params, ExerciseDetail>(){

        @Parcelize
        class Params(val userID:String, val numOfSession: Int) :Parcelable

        override fun createIntent(context: Context, input: Params): Intent {
            return Intent(context,AddExercise::class.java).apply {
                putExtra("userID", input.userID)
                putExtra("numOfSession", input.numOfSession)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ExerciseDetail {

            if(resultCode == RESULT_OK && intent != null){
                val newExerciseID = intent.getIntExtra("eID", 0)
                val newExerciseDate = intent.getStringExtra("eDate")
                val newExerciseName = intent.getStringExtra("eName")
                val newExerciseParcelable = intent.getParcelableArrayListExtra<Parcelable>("ListOfSetDetails")
                val newExerciseList = newExerciseParcelable?.mapNotNull { it as SetDetail } ?: emptyList()

                return ExerciseDetail(newExerciseID,newExerciseDate!!,newExerciseName!!, newExerciseList)

            } else{
                return ExerciseDetail(-1,"","", arrayListOf())
            }
        }

    }

}