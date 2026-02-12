package com.clerk.workouttracker.models

import dev.convex.android.ConvexNum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Workout(
  @SerialName("_id") val id: String,
  val date: String,
  @SerialName("activity") val workoutActivity: WorkoutActivity,
  val duration: @ConvexNum Int? = null,
)

@Serializable
enum class WorkoutActivity {
  Running,
  Lifting,
  Walking,
  Swimming,
}
