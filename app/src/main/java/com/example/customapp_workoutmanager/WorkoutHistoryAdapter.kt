package com.example.customapp_workoutmanager

import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.database.database

class WorkoutHistoryAdapter: RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder>() {

    private var data: MutableList<ExerciseDetail> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.layout_row, parent,false) as View
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)

    }

    class ViewHolder(private val v: View): RecyclerView.ViewHolder(v), View.OnCreateContextMenuListener{
        private val eDate: TextView = v.findViewById(R.id.exerciseDate)
        private val eName: TextView = v.findViewById(R.id.exerciseName)
        private val eCardView: CardView = v.findViewById(R.id.exerciseCardView)

        init{
            v.setOnLongClickListener {
                v.showContextMenu()
                true
            }

            v.setOnCreateContextMenuListener(this)
        }
        fun bind(item :ExerciseDetail){
            eDate.text = item.eDate
            eName.text = item.eName

        }


        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {

            menu?.setHeaderTitle("Select your option")
            menu?.add(absoluteAdapterPosition,R.id.viewExercise,0,"View")
            menu?.add(absoluteAdapterPosition,R.id.deleteExercise,1,"Delete")
        }
    }

    fun removeExercise(position: Int){
        val database = Firebase.database("https://workoutmanager-99900-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("104225962")
        val date = data[position].eDate
        val deleteExerciseID = data[position].eID
        myRef.child(date).child(deleteExerciseID.toString()).removeValue()

        data.remove(data[position])
        notifyDataSetChanged()
    }

    fun updateList(updatedExercise: ExerciseDetail){
        val database = Firebase.database("https://workoutmanager-99900-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("104225962")
        val date = updatedExercise.eDate
        val updatedExerciseID = updatedExercise.eID

        myRef.child(date).child(updatedExerciseID.toString()).setValue(updatedExercise)


        data[updatedExercise.eID - 1] = updatedExercise
        notifyDataSetChanged()
    }


    fun submitList(newList: MutableList<ExerciseDetail>){
        data = newList
        notifyDataSetChanged()
    }





}