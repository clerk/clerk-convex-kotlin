package com.clerk.workouttracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.clerk.workouttracker.models.WorkoutActivity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutEditorScreen(viewModel: WorkoutEditorViewModel, workoutSaved: () -> Unit) {
  var showDatePicker by remember { mutableStateOf(false) }
  var activityDropdownExpanded by remember { mutableStateOf(false) }
  var selectedDate by remember { mutableStateOf(LocalDate.now()) }
  var selectedActivity by remember { mutableStateOf<WorkoutActivity?>(null) }
  var durationRaw by remember { mutableStateOf("") }

  val initialDateMillis = selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
  val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
  val uiState =
    WorkoutEditorUiState(
      selectedDate = selectedDate,
      selectedActivity = selectedActivity,
      durationRaw = durationRaw,
      activityDropdownExpanded = activityDropdownExpanded,
    )
  val actions =
    WorkoutEditorActions(
      onBack = workoutSaved,
      onShowDatePicker = { showDatePicker = true },
      onActivityDropdownExpandedChange = { activityDropdownExpanded = it },
      onActivitySelected = { selectedActivity = it },
      onDurationChanged = { value ->
        if (value.all(Char::isDigit)) {
          durationRaw = value
        }
      },
      onSave = {
        val activity = selectedActivity
        if (activity != null) {
          val duration = durationRaw.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
          viewModel.storeWorkout(
            date = selectedDate.toString(),
            workoutActivity = activity,
            duration = duration,
            onSaved = workoutSaved,
          )
        }
      },
    )

  WorkoutEditorContent(uiState = uiState, actions = actions)

  if (showDatePicker) {
    WorkoutDatePickerDialog(
      datePickerState = datePickerState,
      onDismiss = { showDatePicker = false },
      onDateConfirmed = { selectedDate = it },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutEditorContent(uiState: WorkoutEditorUiState, actions: WorkoutEditorActions) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("New workout") },
        navigationIcon = { TextButton(onClick = actions.onBack) { Text("Back") } },
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
  ) { innerPadding ->
    WorkoutEditorForm(
      uiState = uiState,
      actions = actions,
      modifier =
        Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp, vertical = 12.dp),
    )
  }
}

@Composable
private fun WorkoutEditorForm(
  uiState: WorkoutEditorUiState,
  actions: WorkoutEditorActions,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
    WorkoutDateField(
      selectedDate = uiState.selectedDate,
      onShowDatePicker = actions.onShowDatePicker,
    )
    WorkoutActivityField(
      selectedActivity = uiState.selectedActivity,
      activityDropdownExpanded = uiState.activityDropdownExpanded,
      onActivityDropdownExpandedChange = actions.onActivityDropdownExpandedChange,
      onActivitySelected = actions.onActivitySelected,
    )
    WorkoutDurationField(
      durationRaw = uiState.durationRaw,
      onDurationChanged = actions.onDurationChanged,
    )
    Button(
      onClick = actions.onSave,
      enabled = uiState.selectedActivity != null,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text("Save")
    }
  }
}

@Composable
private fun WorkoutDateField(selectedDate: LocalDate, onShowDatePicker: () -> Unit) {
  OutlinedTextField(
    value = selectedDate.toString(),
    onValueChange = {},
    label = { Text("Date") },
    readOnly = true,
    trailingIcon = { TextButton(onClick = onShowDatePicker) { Text("Pick") } },
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun WorkoutActivityField(
  selectedActivity: WorkoutActivity?,
  activityDropdownExpanded: Boolean,
  onActivityDropdownExpandedChange: (Boolean) -> Unit,
  onActivitySelected: (WorkoutActivity) -> Unit,
) {
  Box(modifier = Modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = selectedActivity?.name ?: "No selection",
      onValueChange = {},
      readOnly = true,
      label = { Text("Activity") },
      trailingIcon = {
        TextButton(onClick = { onActivityDropdownExpandedChange(true) }) { Text("Pick") }
      },
      modifier = Modifier.fillMaxWidth(),
    )
    DropdownMenu(
      expanded = activityDropdownExpanded,
      onDismissRequest = { onActivityDropdownExpandedChange(false) },
    ) {
      WorkoutActivity.entries.forEach { activity ->
        DropdownMenuItem(
          text = { Text(text = activity.name) },
          onClick = {
            onActivitySelected(activity)
            onActivityDropdownExpandedChange(false)
          },
        )
      }
    }
  }
}

@Composable
private fun WorkoutDurationField(durationRaw: String, onDurationChanged: (String) -> Unit) {
  OutlinedTextField(
    value = durationRaw,
    onValueChange = onDurationChanged,
    label = { Text("Duration (optional)") },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    modifier = Modifier.fillMaxWidth(),
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutDatePickerDialog(
  datePickerState: androidx.compose.material3.DatePickerState,
  onDismiss: () -> Unit,
  onDateConfirmed: (LocalDate) -> Unit,
) {
  DatePickerDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(
        onClick = {
          datePickerState.selectedDateMillis?.let { millis ->
            onDateConfirmed(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
          }
          onDismiss()
        }
      ) {
        Text("OK")
      }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  ) {
    DatePicker(state = datePickerState)
  }
}

private data class WorkoutEditorUiState(
  val selectedDate: LocalDate,
  val selectedActivity: WorkoutActivity?,
  val durationRaw: String,
  val activityDropdownExpanded: Boolean,
)

private data class WorkoutEditorActions(
  val onBack: () -> Unit,
  val onShowDatePicker: () -> Unit,
  val onActivityDropdownExpandedChange: (Boolean) -> Unit,
  val onActivitySelected: (WorkoutActivity) -> Unit,
  val onDurationChanged: (String) -> Unit,
  val onSave: () -> Unit,
)
