package com.clerk.workouttracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.clerk.workouttracker.WorkoutTrackerApplication
import com.clerk.workouttracker.core.WorkoutRepository
import com.clerk.workouttracker.models.Workout
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OverviewViewModel(private val repository: WorkoutRepository) : ViewModel() {
  private val selectedWeek = MutableStateFlow(defaultStartDate())

  @OptIn(ExperimentalCoroutinesApi::class)
  val uiState =
    selectedWeek
      .flatMapLatest { week ->
        repository
          .subscribeToWorkoutsInRange(
            startDate = week.toString(),
            endDate = week.plusDays(END_OF_WEEK_OFFSET_DAYS).toString(),
          )
          .map { result ->
            val workouts = result.getOrElse { emptyList() }
            OverviewUiState(
              loading = false,
              selectedWeek = week,
              workoutsForWeek = workouts,
              workoutDays = workouts.toWorkoutDays(),
            )
          }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OverviewUiState(),
      )

  fun selectNextWeek() {
    selectedWeek.value = selectedWeek.value.plusDays(DAYS_IN_WEEK)
  }

  fun selectPreviousWeek() {
    selectedWeek.value = selectedWeek.value.minusDays(DAYS_IN_WEEK)
  }

  fun deleteWorkout(workout: Workout) {
    viewModelScope.launch { repository.deleteWorkout(workoutId = workout.id) }
  }

  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val repository = (this[APPLICATION_KEY] as WorkoutTrackerApplication).repository
        OverviewViewModel(repository)
      }
    }

    private fun defaultStartDate(): LocalDate =
      LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private const val DAYS_IN_WEEK = 7L
    private const val END_OF_WEEK_OFFSET_DAYS = 6L

    private fun List<Workout>.toWorkoutDays(): Set<DayOfWeek> =
      mapNotNull { workout -> runCatching { LocalDate.parse(workout.date).dayOfWeek }.getOrNull() }
        .toSet()
  }
}

data class OverviewUiState(
  val loading: Boolean = true,
  val selectedWeek: LocalDate = LocalDate.now(),
  val workoutsForWeek: List<Workout> = emptyList(),
  val workoutDays: Set<DayOfWeek> = emptySet(),
)
