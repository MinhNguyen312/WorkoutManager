package com.example.customapp_workoutmanager

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SetDetail(val sReps: Int, val sWeight:Int) :Parcelable
