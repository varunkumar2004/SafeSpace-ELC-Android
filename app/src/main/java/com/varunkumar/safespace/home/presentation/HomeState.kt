package com.varunkumar.safespace.home.presentation

import com.varunkumar.safespace.home.data.SensorValues
import com.varunkumar.safespace.shared.SensorDataResponse
import com.varunkumar.safespace.utils.Result

data class HomeState(
    val result: Result<String> = Result.Idle(),
    val sensorValues: SensorValues = SensorValues(),
    val recommendedVideos: List<String>? = null,
    val stressLevel: String? = null,
    val values: List<SensorDataResponse> = emptyList(),
    val showAlert: Boolean = false
)
