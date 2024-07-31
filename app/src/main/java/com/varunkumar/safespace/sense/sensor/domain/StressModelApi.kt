package com.varunkumar.safespace.sense.sensor.domain

import com.varunkumar.safespace.shared.StressLevelResponse
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface StressModelApi {
    @Multipart
    @POST("/")
    fun getStressLevel(
        @Part("snoring_range") snoringRange: Float,
        @Part("respiration_rate") respirationRate: Float,
        @Part("body_temperature") temperature: Float,
        @Part("blood_oxygen") bloodOxygen: Float,
        @Part("sleep") sleep: Float,
        @Part("heart_rate") heartRate: Float
    ): Call<StressLevelResponse>
}
