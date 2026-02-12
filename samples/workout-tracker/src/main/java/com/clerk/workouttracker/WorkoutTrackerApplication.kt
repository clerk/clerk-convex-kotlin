package com.clerk.workouttracker

import android.app.Application
import com.clerk.api.Clerk
import com.clerk.api.ClerkConfigurationOptions
import com.clerk.convex.ClerkConvexClient
import com.clerk.workouttracker.core.WorkoutRepository

class WorkoutTrackerApplication : Application() {
  private lateinit var clerkConvexClient: ClerkConvexClient
  lateinit var repository: WorkoutRepository
    private set

  override fun onCreate() {
    super.onCreate()
    Clerk.initialize(
      context = this,
      publishableKey = Env.clerkPublishableKey,
      options = ClerkConfigurationOptions(enableDebugMode = true),
    )

    clerkConvexClient =
      ClerkConvexClient(deploymentUrl = Env.convexDeploymentUrl, context = applicationContext)
    repository = WorkoutRepository(clerkConvexClient)
  }

  override fun onTerminate() {
    repository.close()
    super.onTerminate()
  }
}
