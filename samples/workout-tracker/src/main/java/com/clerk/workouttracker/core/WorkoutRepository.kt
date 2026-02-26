package com.clerk.workouttracker.core

import com.clerk.workouttracker.models.Workout
import com.clerk.workouttracker.models.WorkoutActivity
import dev.convex.android.AuthState
import dev.convex.android.ConvexClientWithAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class WorkoutRepository(private val convexClient: ConvexClientWithAuth<String>) {
  val authState: StateFlow<AuthState<String>> = convexClient.authState

  fun subscribeToWorkoutsInRange(startDate: String, endDate: String): Flow<Result<List<Workout>>> =
    flow {
      emitAll(
        convexClient.subscribe<List<Workout>>(
          "workouts:getInRange",
          mapOf("startDate" to startDate, "endDate" to endDate),
        )
      )
    }

  suspend fun storeWorkout(date: String, workoutActivity: WorkoutActivity, duration: Int?) {
    val args = mutableMapOf<String, Any>("date" to date, "activity" to workoutActivity.name)
    if (duration != null) {
      args["duration"] = duration
    }
    withContext(Dispatchers.IO) { convexClient.mutation("workouts:store", args) }
  }

  suspend fun deleteWorkout(workoutId: String) {
    withContext(Dispatchers.IO) {
      convexClient.mutation("workouts:remove", mapOf("workoutId" to workoutId))
    }
  }
}
