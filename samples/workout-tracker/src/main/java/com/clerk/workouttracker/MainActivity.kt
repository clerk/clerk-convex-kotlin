package com.clerk.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clerk.ui.auth.AuthView
import com.clerk.workouttracker.ui.AppAuthState
import com.clerk.workouttracker.ui.AuthViewModel
import com.clerk.workouttracker.ui.LoadingScreen
import com.clerk.workouttracker.ui.OverviewScreen
import com.clerk.workouttracker.ui.OverviewViewModel
import com.clerk.workouttracker.ui.WorkoutEditorScreen
import com.clerk.workouttracker.ui.WorkoutEditorViewModel
import com.clerk.workouttracker.ui.theme.ClerkConvexTheme

class MainActivity : ComponentActivity() {
  private val authViewModel by viewModels<AuthViewModel> { AuthViewModel.Factory }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ClerkConvexTheme {
        val navController = rememberNavController()
        val authState by authViewModel.authState.collectAsState(AppAuthState.Loading)

        val initialRoute = authState.toRoute()

        NavHost(navController = navController, startDestination = initialRoute) {
          composable(route = Loading.route) { LoadingScreen() }
          composable(route = SignIn.route) { AuthView() }
          composable(route = Overview.route) {
            val viewModel: OverviewViewModel by viewModels { OverviewViewModel.Factory }
            OverviewScreen(
              viewModel = viewModel,
              onClickAddWorkout = { navController.navigate(WorkoutEditor.route) },
            )
          }
          composable(route = WorkoutEditor.route) {
            val viewModel: WorkoutEditorViewModel by viewModels { WorkoutEditorViewModel.Factory }
            WorkoutEditorScreen(viewModel = viewModel) { navController.popBackStack() }
          }
        }

        // Keep nav state aligned with Clerk auth state changes.
        LaunchedEffect(authState) {
          val targetRoute = authState.toRoute()

          if (navController.currentDestination?.route != targetRoute) {
            navController.navigate(targetRoute) {
              popUpTo(navController.graph.id) { inclusive = true }
              launchSingleTop = true
            }
          }
        }
      }
    }
  }
}

private fun AppAuthState.toRoute(): String =
  when (this) {
    AppAuthState.Loading -> Loading.route
    AppAuthState.SignedIn -> Overview.route
    AppAuthState.SignedOut -> SignIn.route
  }
