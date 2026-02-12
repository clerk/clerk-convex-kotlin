package com.clerk.workouttracker

object Env {
  val clerkPublishableKey: String
    get() = BuildConfig.WORKOUT_CLERK_PUBLISHABLE_KEY

  val convexDeploymentUrl: String
    get() = BuildConfig.WORKOUT_CONVEX_DEPLOYMENT_URL
}
