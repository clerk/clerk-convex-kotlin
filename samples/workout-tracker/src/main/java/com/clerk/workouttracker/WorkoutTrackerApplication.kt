package com.clerk.workouttracker

import android.app.Application
import com.clerk.api.Clerk
import com.clerk.api.ClerkConfigurationOptions
import com.clerk.convex.createClerkConvexClient
import com.clerk.workouttracker.core.WorkoutRepository
import dev.convex.android.ConvexClientWithAuth

class WorkoutTrackerApplication : Application() {
  private lateinit var convexClient: ConvexClientWithAuth<String>
  lateinit var repository: WorkoutRepository
    private set

  override fun onCreate() {
    super.onCreate()
    Clerk.initialize(
      context = this,
      publishableKey = Env.clerkPublishableKey,
      options = ClerkConfigurationOptions(enableDebugMode = true),
    )

    convexClient =
      createClerkConvexClient(deploymentUrl = Env.convexDeploymentUrl, context = applicationContext)
    repository = WorkoutRepository(convexClient)
  }
}
