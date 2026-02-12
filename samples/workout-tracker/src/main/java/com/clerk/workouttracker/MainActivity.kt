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
import com.clerk.workouttracker.ui.AuthViewModel
import com.clerk.workouttracker.ui.LoadingScreen
import com.clerk.workouttracker.ui.OverviewScreen
import com.clerk.workouttracker.ui.OverviewViewModel
import com.clerk.workouttracker.ui.SignInScreen
import com.clerk.workouttracker.ui.WorkoutEditorScreen
import com.clerk.workouttracker.ui.WorkoutEditorViewModel
import com.clerk.workouttracker.ui.theme.ClerkConvexTheme
import dev.convex.android.AuthState

class MainActivity : ComponentActivity() {
  private val authViewModel by viewModels<AuthViewModel> { AuthViewModel.Factory }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ClerkConvexTheme {
        val navController = rememberNavController()
        val authState by authViewModel.authState.collectAsState(AuthState.AuthLoading())

        val initialRoute =
          when (authState) {
            is AuthState.AuthLoading -> Loading.route
            is AuthState.Authenticated -> Overview.route
            is AuthState.Unauthenticated -> SignIn.route
          }

        NavHost(navController = navController, startDestination = initialRoute) {
          composable(route = Loading.route) { LoadingScreen() }
          composable(route = SignIn.route) { SignInScreen() }
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
          val targetRoute =
            when (authState) {
              is AuthState.AuthLoading -> Loading.route
              is AuthState.Authenticated -> Overview.route
              is AuthState.Unauthenticated -> SignIn.route
            }

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
