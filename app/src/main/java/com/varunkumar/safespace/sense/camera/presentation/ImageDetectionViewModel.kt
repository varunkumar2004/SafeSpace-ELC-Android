package com.varunkumar.safespace.sense.camera.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.varunkumar.safespace.sense.camera.domain.EmotionDetectionModelImpl
import com.varunkumar.safespace.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ImageDetectionViewModel @Inject constructor(
    private val emotionModelImpl: EmotionDetectionModelImpl
) : ViewModel() {
    private val _state = MutableStateFlow<Result<Boolean>>(Result.Idle())
    private val _image = MutableStateFlow<Bitmap?>(null)
    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _state.flatMapLatest {
        Log.d("camera state change", it.toString())
        _state
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Idle())

    val image = _image.asStateFlow()

    private fun process(bitmap: Bitmap) = emotionModelImpl.process(bitmap)

    fun predict(bitmap: Bitmap) {
        _state.update { Result.Loading() }

        viewModelScope.launch {
            cropFaceBitmap(bitmap)?.let { image ->
                process(image).catch { e ->
                    Log.e("model error", e.localizedMessage ?: "model input error")
                    _state.update { Result.Error(e.localizedMessage) }
                }.collect { bool ->
                    if (bool) _state.update {
                        Result.Success(bool)
                    } else _state.update {
                        Result.Error("No Stress Detected.")
                    }
                }
            }
        }
    }

    private fun updateImage(newImage: Bitmap) {
        _image.update { newImage }
    }
}

private suspend fun cropFaceBitmap(
    inputBitmap: Bitmap
): Bitmap? {
    var outputBitmap: Bitmap? = null

    val inputImage = InputImage.fromBitmap(inputBitmap, 0)
    val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()
    val detector = FaceDetection.getClient(options)

    detector.process(inputImage)
        .addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) {
                val face = faces[0]
                val bounds = face.boundingBox

                outputBitmap = Bitmap.createBitmap(
                    inputBitmap,
                    bounds.left.coerceAtLeast(0),
                    bounds.top.coerceAtLeast(0),
                    bounds.width().coerceAtMost(inputBitmap.width - bounds.left),
                    bounds.height().coerceAtMost(inputBitmap.height - bounds.top)
                )

                outputBitmap?.let {
                    outputBitmap =  Bitmap.createScaledBitmap(it, 64, 64, true)
                }
            }
        }
        .addOnFailureListener { e ->
            Log.e("error", "$e")
        }.await()

    return outputBitmap
}
