package com.example.customapp_workoutmanager

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseDetail(val eID: Int, val eDate: String, val eName: String, val eSetList: List<SetDetail>):Parcelable
