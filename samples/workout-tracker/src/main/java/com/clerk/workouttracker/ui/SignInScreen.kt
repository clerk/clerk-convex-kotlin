package com.clerk.workouttracker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clerk.ui.auth.AuthView
import com.clerk.workouttracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen() {
  var authViewIsPresented by rememberSaveable { mutableStateOf(false) }

  Column(
    modifier =
      Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_launcher_foreground),
      contentDescription = null,
      modifier = Modifier.size(112.dp),
    )
    Text(
      text = "Workout Tracker",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(top = 16.dp, bottom = 20.dp),
    )
    Button(onClick = { authViewIsPresented = true }) { Text("Login") }
  }

  if (authViewIsPresented) {
    ModalBottomSheet(onDismissRequest = { authViewIsPresented = false }) {
      AuthView(modifier = Modifier.fillMaxWidth())
    }
  }
}
