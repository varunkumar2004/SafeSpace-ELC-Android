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
        @Part("snoring_range") snoringRange: Float = 85f,
        @Part("respiration_rate") respirationRate: Float = 12f,
        @Part("body_temperature") temperature: Float = 97f,
        @Part("blood_oxygen") bloodOxygen: Float = 105f,
        @Part("sleep") sleep: Float = 1f,
        @Part("heart_rate") heartRate: Float = 100f
    ): Call<StressLevelResponse>
}
