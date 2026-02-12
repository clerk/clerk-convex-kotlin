package com.clerk.workouttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.clerk.workouttracker.WorkoutTrackerApplication
import com.clerk.workouttracker.core.WorkoutRepository
import com.clerk.workouttracker.models.Activity
import kotlinx.coroutines.launch

class WorkoutEditorViewModel(private val repository: WorkoutRepository) : ViewModel() {
  fun storeWorkout(date: String, activity: Activity, duration: Int?, onSaved: () -> Unit) {
    viewModelScope.launch {
      runCatching { repository.storeWorkout(date = date, activity = activity, duration = duration) }
        .onSuccess { onSaved() }
    }
  }

  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val repository = (this[APPLICATION_KEY] as WorkoutTrackerApplication).repository
        WorkoutEditorViewModel(repository)
      }
    }
  }
}
