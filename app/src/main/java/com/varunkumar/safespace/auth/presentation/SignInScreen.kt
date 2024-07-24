package com.varunkumar.safespace.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.varunkumar.safespace.R
import com.varunkumar.safespace.ui.theme.secondary
import com.varunkumar.safespace.ui.theme.surface

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    state: SignInState,
    onSignInClick: () -> Unit,
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.signInError) {
        state.signInError?.let { error ->
            snackBarHostState.showSnackbar(message = error, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = secondary,
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent
            ) {
                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(TextFieldDefaults.MinHeight),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = surface
                    ),
                    onClick = onSignInClick
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = "Sign In", style = MaterialTheme.typography.titleLarge)
                }
            }
        },
        snackbarHost = {
            snackBarHostState.currentSnackbarData?.let { Snackbar(snackbarData = it) }
        }
    ) {
        Box(
            modifier = modifier.padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Image(
                    modifier = Modifier.size(150.dp),
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = null
                )

                Text(
                    text = "Safespace",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black
                )
            }
        }
    }
}