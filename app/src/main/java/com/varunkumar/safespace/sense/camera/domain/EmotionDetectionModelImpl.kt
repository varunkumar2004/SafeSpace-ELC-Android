package com.varunkumar.safespace.sense.camera.domain

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.varunkumar.safespace.ml.EmotionDetection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class EmotionDetectionModelImpl(
    context: Context
) {
    private val model = EmotionDetection.newInstance(context)
    private var imageProcessor: ImageProcessor? = null
    private var inputFeature: TensorBuffer? = null
    private var tensorImage = TensorImage(DataType.FLOAT32)

    fun process(bitmap: Bitmap) = flow {
        try {
            val height = bitmap.height
            val width = bitmap.width

            imageProcessor = ImageProcessor.Builder()
                .add(
                    ResizeOp(height, width, ResizeOp.ResizeMethod.BILINEAR)
                )
                .build()

            tensorImage.load(bitmap)
            inputFeature =
                TensorBuffer.createFixedSize(intArrayOf(1, height, width, 3), DataType.FLOAT32)
            tensorImage = imageProcessor!!.process(tensorImage)
            inputFeature!!.loadBuffer(tensorImage.buffer)

            val outputs = model
                .process(inputFeature!!)
                .outputFeature0AsTensorBuffer
                .floatArray

            val boolRes = findIndexOfMaxValue(outputs)

            for (output in outputs) {
                Log.e("model float output", output.toString())
            }

            emit(boolRes)
        } catch (e: Exception) {
            Log.e("model cant predict", e.localizedMessage ?: "Some error occurred")
            emit(false)
        } finally {
            model.close()
        }
    }.flowOn(Dispatchers.IO)
}

private fun findIndexOfMaxValue(floatArray: FloatArray): Boolean {
    val emotions =
        arrayOf("anger", "contempt", "disgust", "fear", "happy", "sadness", "surprise")
    val stressEmotions = arrayOf("anger", "disgust", "fear", "sadness")

    return if (floatArray.isEmpty()) {
        false
    } else {
        var maxIndex = 0
        var maxValue = floatArray[0]

        for (i in 1 until floatArray.size) {
            if (floatArray[i] > maxValue) {
                maxValue = floatArray[i]
                maxIndex = i
            }
        }

        emotions[maxIndex] in stressEmotions
    }
}