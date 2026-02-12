package com.clerk.workouttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.clerk.workouttracker.WorkoutTrackerApplication
import com.clerk.workouttracker.core.WorkoutRepository

class AuthViewModel(repository: WorkoutRepository) : ViewModel() {
  val authState = repository.authState

  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val repository = (this[APPLICATION_KEY] as WorkoutTrackerApplication).repository
        AuthViewModel(repository)
      }
    }
  }
}
