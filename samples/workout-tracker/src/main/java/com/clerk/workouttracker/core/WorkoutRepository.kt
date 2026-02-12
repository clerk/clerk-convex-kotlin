package com.clerk.workouttracker.core

import com.clerk.convex.ClerkConvexClient
import com.clerk.workouttracker.models.Activity
import com.clerk.workouttracker.models.Workout
import dev.convex.android.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class WorkoutRepository(private val clerkConvex: ClerkConvexClient) {
  val authState: StateFlow<AuthState<String>> = clerkConvex.convex.authState

  fun subscribeToWorkoutsInRange(startDate: String, endDate: String): Flow<Result<List<Workout>>> =
    clerkConvex.convex.subscribe<List<Workout>>(
      "workouts:getInRange",
      mapOf("startDate" to startDate, "endDate" to endDate),
    )

  suspend fun storeWorkout(date: String, activity: Activity, duration: Int?) {
    val args = mutableMapOf<String, Any>("date" to date, "activity" to activity.name)
    if (duration != null) {
      args["duration"] = duration
    }
    clerkConvex.convex.mutation<String>("workouts:store", args)
  }

  suspend fun deleteWorkout(workoutId: String) {
    clerkConvex.convex.mutation<String>("workouts:remove", mapOf("workoutId" to workoutId))
  }

  fun close() {
    clerkConvex.close()
  }
}
