package com.varunkumar.safespace.sense.camera.presentation

import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.varunkumar.safespace.sense.camera.presentation.components.CameraPreview
import com.varunkumar.safespace.shared.Routes
import com.varunkumar.safespace.ui.theme.secondary
import com.varunkumar.safespace.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    state: Result<Boolean>,
    controller: LifecycleCameraController,
    navController: NavHostController,
    onCameraClick: () -> Unit,
    onBackCameraClick: () -> Unit
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(state) {
        when (state) {
            is Result.Success -> {
                if (state.data!!) {
                    navController.navigate(Routes.Sense.route)
                }
            }

            is Result.Loading -> {
                snackBarHostState.showSnackbar(
                    message = "Loading...",
                    duration = SnackbarDuration.Short
                )
            }

            is Result.Error -> {
                snackBarHostState.showSnackbar(
                    message = state.msg ?: "Something unexpected happened.",
                    duration = SnackbarDuration.Short
                )
            }

            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = {
            snackBarHostState.currentSnackbarData?.let { Snackbar(it) }
        },
        modifier = modifier,
        containerColor = secondary,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        text = "Image Detection",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackCameraClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        modifier = Modifier.size(50.dp),
                        onClick = onCameraClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) { padding ->
        val shape = RoundedCornerShape(30.dp)

        Box(
            modifier = modifier
        ) {
            CameraPreview(
                controller = controller,
                modifier = modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .clip(shape)
            )
        }
    }
}

