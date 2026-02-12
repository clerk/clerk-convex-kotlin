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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkoutEditorViewModel(private val repository: WorkoutRepository) : ViewModel() {
  private val _uiState = MutableStateFlow(WorkoutEditorSaveUiState())
  val uiState = _uiState.asStateFlow()

  fun storeWorkout(date: String, activity: Activity, duration: Int?, onSaved: () -> Unit) {
    if (_uiState.value.isSaving) return

    _uiState.update { state -> state.copy(isSaving = true, errorMessage = null) }
    viewModelScope.launch {
      runCatching { repository.storeWorkout(date = date, activity = activity, duration = duration) }
        .onSuccess { onSaved() }
        .onFailure { throwable ->
          _uiState.update { state ->
            state.copy(
              errorMessage =
                throwable.message ?: "Could not save workout. Check your connection and try again."
            )
          }
        }
      _uiState.update { state -> state.copy(isSaving = false) }
    }
  }

  fun clearError() {
    _uiState.update { state -> state.copy(errorMessage = null) }
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

data class WorkoutEditorSaveUiState(
  val isSaving: Boolean = false,
  val errorMessage: String? = null,
)
