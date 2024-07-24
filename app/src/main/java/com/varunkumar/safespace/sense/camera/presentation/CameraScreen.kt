package com.varunkumar.safespace.sense.camera.presentation

import android.graphics.Bitmap
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.varunkumar.safespace.sense.camera.presentation.components.CameraPreview
import com.varunkumar.safespace.shared.Routes
import com.varunkumar.safespace.ui.theme.secondary
import com.varunkumar.safespace.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    state: Result<Boolean>,
    image: Bitmap?,
    controller: LifecycleCameraController,
    navController: NavHostController,
    onCameraClick: () -> Unit,
    onBackCameraClick: () -> Unit
) {
    when (state) {
        is Result.Success -> {
            navController.navigate(Routes.Sense.route)
        }

        else -> {}
    }

    Scaffold(
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
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                AsyncImage(
                    model = image,
                    modifier = Modifier.size(100.dp),
                    contentDescription = null
                )
            }
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

