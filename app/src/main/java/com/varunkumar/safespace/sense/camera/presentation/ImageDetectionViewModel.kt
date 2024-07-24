package com.varunkumar.safespace.sense.camera.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varunkumar.safespace.sense.camera.domain.EmotionDetectionModelImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.varunkumar.safespace.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ImageDetectionViewModel @Inject constructor(
    private val emotionModelImpl: EmotionDetectionModelImpl,
) : ViewModel() {
    private val _state = MutableStateFlow<Result<Boolean>>(Result.Idle())
    private val _image = MutableStateFlow<Bitmap?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _state.flatMapLatest {
        Log.d("camera state change", it.toString())
        _state
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Result.Idle())

    val image = _image.asStateFlow()

    private fun process(bitmap: Bitmap) = emotionModelImpl.process(bitmap)

    fun predict(bitmap: Bitmap) {
        viewModelScope.launch {
            process(bitmap).catch { e ->
                Log.e("model error", e.localizedMessage?:"model input error")
                _state.update { Result.Error(e.localizedMessage) }
            }.collect { bool ->
                if (bool) _state.update { Result.Success(true) }
                else _state.update { Result.Idle() }
            }
        }
    }

    fun updateImage(newImage: Bitmap) {
        _image.update { newImage }
    }
}

//private suspend fun cropFaceBitmap(
//    inputBitmap: Bitmap
//): Bitmap? {
//    var outputBitmap: Bitmap? = null
//
//    val inputImage = InputImage.fromBitmap(inputBitmap, 0)
//    val options = FaceDetectorOptions.Builder()
//        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//        .build()
//    val detector = FaceDetection.getClient(options)
//
//    detector.process(inputImage)
//        .addOnSuccessListener { faces ->
//            if (faces.isNotEmpty()) {
//                val face = faces[0]
//                val bounds = face.boundingBox
//                outputBitmap = cropAndResizeBitmap(inputBitmap, bounds)
//            }
//        }
//        .addOnFailureListener { e ->
//            Log.e("error", "$e")
//        }.await()
//
//    return outputBitmap
//}
//
//private fun cropAndResizeBitmap(bitmap: Bitmap, bounds: Rect): Bitmap {
//    val croppedBitmap = Bitmap.createBitmap(
//        bitmap,
//        bounds.left,
//        bounds.top,
//        224,
//        224
//    )
//
//    return Bitmap.createScaledBitmap(croppedBitmap, 224, 224, true)
//}