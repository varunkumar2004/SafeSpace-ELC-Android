package com.varunkumar.safespace.sense.sensor.model

sealed class HealthSensors(
    val label: String,
    val low: Float,
    val high: Float
) {
    data object SnoringRateSensors : HealthSensors("Snoring Rate", 45f, 100f)
    data object RespirationRateSensors : HealthSensors("Respiration Rate", 16f, 48.56f)
    data object HoursOfSleepSensors : HealthSensors("Sleep", 50f, 158.65f)
}