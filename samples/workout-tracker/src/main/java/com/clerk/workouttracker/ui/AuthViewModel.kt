package com.clerk.workouttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.clerk.api.Clerk
import com.clerk.api.session.Session.SessionStatus
import com.clerk.workouttracker.WorkoutTrackerApplication
import com.clerk.workouttracker.core.WorkoutRepository
import dev.convex.android.AuthState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest

class AuthViewModel(repository: WorkoutRepository) : ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val authState =
    combine(repository.authState, Clerk.isInitialized, Clerk.sessionFlow) {
        convexAuthState,
        clerkInitialized,
        session ->
        when (convexAuthState) {
          is AuthState.Authenticated -> AppAuthState.SignedIn
          is AuthState.AuthLoading -> AppAuthState.Loading
          is AuthState.Unauthenticated ->
            when {
              !clerkInitialized -> AppAuthState.Loading
              session?.status == SessionStatus.ACTIVE -> AppAuthState.Loading
              else -> AppAuthState.SignedOut
            }
        }
      }
      .transformLatest { state ->
        if (state == AppAuthState.SignedOut) {
          delay(SIGNED_OUT_TRANSITION_DELAY_MS)
        }
        emit(state)
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppAuthState.Loading,
      )

  companion object {
    private const val SIGNED_OUT_TRANSITION_DELAY_MS = 250L

    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val repository = (this[APPLICATION_KEY] as WorkoutTrackerApplication).repository
        AuthViewModel(repository)
      }
    }
  }
}

enum class AppAuthState {
  Loading,
  SignedIn,
  SignedOut,
}
