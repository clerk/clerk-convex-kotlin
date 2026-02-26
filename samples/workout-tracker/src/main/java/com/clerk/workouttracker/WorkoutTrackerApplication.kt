package com.clerk.workouttracker

import android.app.Application
import com.clerk.api.Clerk
import com.clerk.api.ClerkConfigurationOptions
import com.clerk.convex.ClerkConvexAuthProvider
import com.clerk.convex.ConvexClientWithAuth
import com.clerk.workouttracker.core.WorkoutRepository
import dev.convex.android.ConvexClientWithAuth

class WorkoutTrackerApplication : Application() {
  private lateinit var convexClient: ConvexClientWithAuth<String>
  private lateinit var authProvider: ClerkConvexAuthProvider
  lateinit var repository: WorkoutRepository
    private set

  override fun onCreate() {
    super.onCreate()
    Clerk.initialize(
      context = this,
      publishableKey = Env.clerkPublishableKey,
      options = ClerkConfigurationOptions(enableDebugMode = true),
    )

    authProvider = ClerkConvexAuthProvider()
    convexClient =
      ConvexClientWithAuth(
        deploymentUrl = Env.convexDeploymentUrl,
        authProvider = authProvider,
        context = applicationContext,
      )
    repository = WorkoutRepository(convexClient)
  }

  override fun onTerminate() {
    authProvider.close()
    super.onTerminate()
  }
}
