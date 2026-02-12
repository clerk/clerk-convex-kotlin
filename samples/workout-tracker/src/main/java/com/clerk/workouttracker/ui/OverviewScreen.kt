package com.clerk.workouttracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clerk.ui.userbutton.UserButton
import com.clerk.workouttracker.models.Workout
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val weekDateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
private val dayOfWeekLetters =
  mapOf(
    DayOfWeek.MONDAY to "M",
    DayOfWeek.TUESDAY to "T",
    DayOfWeek.WEDNESDAY to "W",
    DayOfWeek.THURSDAY to "T",
    DayOfWeek.FRIDAY to "F",
    DayOfWeek.SATURDAY to "S",
    DayOfWeek.SUNDAY to "S",
  )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(viewModel: OverviewViewModel, onClickAddWorkout: () -> Unit) {
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
    topBar = { TopAppBar(title = { Text("Workouts") }, actions = { UserButton() }) },
    containerColor = MaterialTheme.colorScheme.background,
  ) { innerPadding ->
    Column(
      modifier =
        Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      WorkoutDateSelector(
        selectedStartOfWeek = uiState.selectedWeek,
        onPreviousWeek = viewModel::selectPreviousWeek,
        onNextWeek = viewModel::selectNextWeek,
      )
      WorkoutCalendar(workoutDays = uiState.workoutDays)
      WorkoutList(
        workouts = uiState.workoutsForWeek,
        onDeleteWorkout = viewModel::deleteWorkout,
        modifier = Modifier.weight(1f),
      )
      Button(onClick = onClickAddWorkout, modifier = Modifier.fillMaxWidth()) {
        Text("Add Workout")
      }
    }
  }
}

@Composable
private fun WorkoutDateSelector(
  selectedStartOfWeek: LocalDate,
  onPreviousWeek: () -> Unit,
  onNextWeek: () -> Unit,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    TextButton(onClick = onPreviousWeek) { Text("<") }
    Text(
      text = "Week of ${selectedStartOfWeek.format(weekDateFormatter)}",
      textAlign = TextAlign.Center,
      modifier = Modifier.weight(1f),
      style = MaterialTheme.typography.titleMedium,
    )
    TextButton(onClick = onNextWeek) { Text(">") }
  }
}

@Composable
private fun WorkoutCalendar(workoutDays: Set<DayOfWeek>) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    DayOfWeek.entries.forEach { day ->
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = dayOfWeekLetters[day] ?: day.name.first().toString(),
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = if (workoutDays.contains(day)) "●" else "○",
          style = MaterialTheme.typography.bodyLarge,
        )
      }
    }
  }
}

@Composable
private fun WorkoutList(
  workouts: List<Workout>,
  onDeleteWorkout: (Workout) -> Unit,
  modifier: Modifier = Modifier,
) {
  if (workouts.isEmpty()) {
    Column(
      modifier = modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Text("No workouts for this week yet.")
    }
    return
  }

  LazyColumn(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    items(items = workouts, key = { workout -> workout.id }) { workout ->
      WorkoutRow(workout = workout, onDelete = { onDeleteWorkout(workout) })
    }
  }
}

@Composable
private fun WorkoutRow(workout: Workout, onDelete: () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
      Text(
        text =
          runCatching { LocalDate.parse(workout.date).format(weekDateFormatter) }
            .getOrDefault(workout.date),
        style = MaterialTheme.typography.labelLarge,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = workout.activity.name, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        workout.duration?.let { duration ->
          val suffix = if (duration == 1) "" else "s"
          Text(text = "$duration min$suffix", style = MaterialTheme.typography.bodyMedium)
          Spacer(modifier = Modifier.width(8.dp))
        }
        TextButton(onClick = onDelete) { Text("Delete") }
      }
    }
  }
}
