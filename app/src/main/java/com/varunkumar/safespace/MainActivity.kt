package com.varunkumar.safespace

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.varunkumar.safespace.auth.domain.GoogleAuthClient
import com.varunkumar.safespace.auth.presentation.SignInViewModel
import com.varunkumar.safespace.auth.presentation.SignInScreen
import com.varunkumar.safespace.chat.presentation.ChatScreen
import com.varunkumar.safespace.home.presentation.HomeScreen
import com.varunkumar.safespace.home.presentation.HomeViewModel
import com.varunkumar.safespace.sense.camera.presentation.CameraScreen
import com.varunkumar.safespace.sense.camera.presentation.ImageDetectionViewModel
import com.varunkumar.safespace.sense.sensor.presentation.SenseScreen
import com.varunkumar.safespace.shared.Routes
import com.varunkumar.safespace.shared.SharedViewModelData
import com.varunkumar.safespace.shared.UserData
import com.varunkumar.safespace.ui.theme.SafespaceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SafespaceTheme {
                if (!hasRequiredPermissions()) {
                    ActivityCompat.requestPermissions(this, permissions, 0)
                }

                val sModifier = Modifier.fillMaxSize()
                val navController = rememberNavController()
                val homeViewModel = hiltViewModel<HomeViewModel>()
                val snackBarHostState = remember { SnackbarHostState() }

                val startDestination =
                    if (googleAuthClient.getSignedInUser() == null) Routes.Login.route
                    else Routes.Sense.route

                NavHost(navController = navController, startDestination = startDestination) {
                    composable(route = Routes.Home.route) {
                        HomeScreen(
                            modifier = sModifier,
                            userData = googleAuthClient.getSignedInUser(),
                            viewModel = homeViewModel,
                            onChatNavigate = { navController.navigate(Routes.Chat.route) },
                            onSenseNavigate = { navController.navigate(Routes.Camera.route) },
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthClient.signOut()
                                    navController.navigate(Routes.Login.route)
                                }
                            }
                        )
                    }

                    composable(route = Routes.Sense.route) {
                        val sensorDataResponse = homeViewModel.state.collectAsState().value.values

                        SenseScreen(
                            modifier = sModifier,
                            sensorValues = sensorDataResponse,
                            onDoneButtonClick = { navController.navigate(Routes.Home.route) }
                        )
                    }

                    composable(route = Routes.Camera.route) {
                        val viewModel = hiltViewModel<ImageDetectionViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle().value
                        val image by viewModel.image.collectAsStateWithLifecycle()
                        val cameraController = remember {
                            LifecycleCameraController(applicationContext).apply {
                                setEnabledUseCases(CameraController.IMAGE_CAPTURE)
                                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                        }

                        CameraScreen(
                            modifier = sModifier,
                            state = state,
                            image = image,
                            controller = cameraController,
                            navController = navController,
                            onBackCameraClick = { navController.navigateUp() },
                            onCameraClick = {
                                takePhoto(
                                    controller = cameraController,
                                    onPhotoTaken = { bitmap ->
                                        viewModel.predict(bitmap)
                                        viewModel.updateImage(bitmap)
                                    }
                                )
                            }
                        )
                    }

                    composable(route = Routes.Chat.route) {
                        ChatScreen(
                            modifier = sModifier,
                            onBackButtonClick = { navController.navigateUp() }
                        )
                    }

                    composable(Routes.Login.route) {
                        val signInViewModel = hiltViewModel<SignInViewModel>()
                        val state by signInViewModel.state.collectAsStateWithLifecycle()

                        val launcher =
                            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult =
                                            googleAuthClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                        signInViewModel.onSignInResult(signInResult)
                                    }
                                }
                            }

                        LaunchedEffect(state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                lifecycleScope.launch {
                                    snackBarHostState.showSnackbar(
                                        message = "Sign in successful",
                                        duration = SnackbarDuration.Short
                                    )
                                }

                                navController.popBackStack()
                                navController.navigate(Routes.Home.route)
                                // on Sign in Successful popbackstack()
                            }
                        }

                        SignInScreen(
                            modifier = sModifier,
                            state = state,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun takePhoto(
        controller: LifecycleCameraController,
        onPhotoTaken: (Bitmap) -> Unit
    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }
                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )

                    onPhotoTaken(rotatedBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo: ", exception)
                }
            }
        )
    }

    private fun hasRequiredPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val permissions = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}