package com.varunkumar.safespace.sense.sensor.presentation

import com.varunkumar.safespace.home.data.SensorValues
import com.varunkumar.safespace.utils.Result

data class SenseState(
    val result: Result<String> = Result.Idle(),
    val sliderValues: SensorValues = SensorValues(),
    val questionFieldText: String = "",
    val questionIndex: Int = 0
)