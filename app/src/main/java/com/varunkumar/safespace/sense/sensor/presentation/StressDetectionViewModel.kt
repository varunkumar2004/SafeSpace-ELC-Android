package com.varunkumar.safespace.sense.sensor.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.varunkumar.safespace.sense.sensor.domain.StressModelApi
import com.varunkumar.safespace.sense.sensor.data.HealthSensors
import com.varunkumar.safespace.shared.SensorDataResponse
import com.varunkumar.safespace.shared.SharedViewModelData
import com.varunkumar.safespace.shared.StressLevelResponse
import com.varunkumar.safespace.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class StressDetectionViewModel @Inject constructor(
    private val stressModelApi: StressModelApi,
    private val sharedViewModelData: SharedViewModelData
) : ViewModel() {
    private val _state = MutableStateFlow(SenseState())
    val state = _state.asStateFlow()

    fun sliderChange(newValue: Float, sensor: HealthSensors) {
        when (sensor) {
            is HealthSensors.SnoringRateSensors -> {
                _state.update { it.copy(sliderValues = it.sliderValues.copy(snoringRate = newValue)) }
            }

            is HealthSensors.RespirationRateSensors -> {
                _state.update { it.copy(sliderValues = it.sliderValues.copy(respirationRate = newValue)) }
            }

            is HealthSensors.HoursOfSleepSensors -> {
                _state.update { it.copy(sliderValues = it.sliderValues.copy(sleepHours = newValue)) }
            }
        }
    }

    fun predictStress(senseValues: List<SensorDataResponse>) {
        _state.update { it.copy(result = Result.Loading()) }
        val meanSensorDataResponse = getMeanSenseValues(senseValues)

        stressModelApi.getStressLevel(
            snoringRange = _state.value.sliderValues.snoringRate,
            respirationRate = _state.value.sliderValues.snoringRate,
            sleep = _state.value.sliderValues.snoringRate,
            heartRate = meanSensorDataResponse.heartRate,
            bloodOxygen = meanSensorDataResponse.spO2.toFloat(),
            temperature = meanSensorDataResponse.temperature
        ).enqueue(
            object : Callback<StressLevelResponse?> {
                override fun onResponse(
                    p0: Call<StressLevelResponse?>,
                    result: Response<StressLevelResponse?>
                ) {
                    viewModelScope.launch {
                        result.body()?.let { res ->
                            sharedViewModelData.liveStressLevel.emit(res)
                            _state.update { it.copy(result = Result.Success(res.stressLevel)) }
                        }
                    }
                }

                override fun onFailure(p0: Call<StressLevelResponse?>, error: Throwable) {
                    _state.update { it.copy(result = Result.Error(error.localizedMessage)) }
                }
            }
        )
    }
}

private fun getMeanSenseValues(values: List<SensorDataResponse>): SensorDataResponse {
    val meanSensorDataResponse = SensorDataResponse(0f, 0, 0f)
    var len = 0

    for (value in values) {
        meanSensorDataResponse.apply {
            heartRate += value.heartRate
            spO2 += value.spO2
            temperature += value.temperature
        }
        len++
    }

    return meanSensorDataResponse.apply {
        heartRate /= len
        spO2 /= len
        temperature /= len
    }
}

