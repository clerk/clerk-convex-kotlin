package com.clerk.workouttracker

interface WorkoutTrackerDestination {
  val route: String
  val title: String
}

object SignIn : WorkoutTrackerDestination {
  override val route = "sign_in"
  override val title = "Welcome"
}

object Loading : WorkoutTrackerDestination {
  override val route = "loading"
  override val title = "Loading"
}

object Overview : WorkoutTrackerDestination {
  override val route = "overview"
  override val title = "Workouts"
}

object WorkoutEditor : WorkoutTrackerDestination {
  override val route = "workout_editor"
  override val title = "New workout"
}
