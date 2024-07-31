package com.varunkumar.safespace.sense.sensor.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.varunkumar.safespace.R
import com.varunkumar.safespace.sense.sensor.data.HealthSensors
import com.varunkumar.safespace.sense.sensor.presentation.components.CustomSlider
import com.varunkumar.safespace.sense.sensor.presentation.components.PhysicalSensorValues
import com.varunkumar.safespace.shared.SensorDataResponse
import com.varunkumar.safespace.ui.theme.primary
import com.varunkumar.safespace.ui.theme.secondary
import com.varunkumar.safespace.ui.theme.surface
import com.varunkumar.safespace.utils.GridView

@Composable
fun SenseScreen(
    modifier: Modifier = Modifier,
    sensorValues: List<SensorDataResponse>,
    onDoneButtonClick: () -> Unit
) {
    val stressDetectionViewModel = hiltViewModel<StressDetectionViewModel>()
    val senseState = stressDetectionViewModel.state.collectAsStateWithLifecycle().value
    val fModifier = Modifier.fillMaxWidth()

    Scaffold(
        contentColor = Color.Black,
        containerColor = primary,
        topBar = {
            TopAppBar(modifier = fModifier)
        }
    ) {
        Column(
            modifier = modifier
                .padding(it)
        ) {
            GridView(
                modifier = modifier,
                bottomBackground = secondary,
                topGridView = { modifier ->
                    Column(
                        modifier = modifier,
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .size(200.dp),
                            painter = painterResource(id = R.drawable.hardware_illus),
                            contentDescription = null
                        )
                    }
                },
                bottomGridView = { modifier ->
                    BottomSliderBox(
                        modifier = modifier,
                        viewModel = stressDetectionViewModel,
                        state = senseState,
                        sensorValues = sensorValues,
                        onPredictButtonClick = {
                            stressDetectionViewModel.predictStress(sensorValues)
                            onDoneButtonClick()
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(
                text = "Realtime Detection",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    )
}

@Composable
private fun BottomSliderBox(
    modifier: Modifier = Modifier,
    viewModel: StressDetectionViewModel,
    sensorValues: List<SensorDataResponse>,
    onPredictButtonClick: () -> Unit,
    state: SenseState
) {
    val fModifier = Modifier.fillMaxWidth()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = fModifier.align(Alignment.TopCenter),
            textAlign = TextAlign.Center,
            text = "Physical Values",
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )

        Column(
            modifier = fModifier.align(Alignment.Center)
        ) {
            PhysicalSensorValues(
                modifier = Modifier
                    .fillMaxWidth(),
                response = if (sensorValues.isEmpty())
                    SensorDataResponse(0f, 0, 0f)
                else sensorValues.last(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomSlider(
                modifier = fModifier,
                viewModel = viewModel,
                sliderPosition = state.sliderValues.snoringRate,
                sensor = HealthSensors.SnoringRateSensors
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomSlider(
                modifier = fModifier,
                viewModel = viewModel,
                sliderPosition = state.sliderValues.sleepHours,
                sensor = HealthSensors.HoursOfSleepSensors
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomSlider(
                modifier = fModifier,
                viewModel = viewModel,
                sliderPosition = state.sliderValues.respirationRate,
                sensor = HealthSensors.RespirationRateSensors
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = fModifier
                    .height(TextFieldDefaults.MinHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = surface,
                    contentColor = Color.White
                ),
                onClick = onPredictButtonClick
            ) {
                Text(
                    text = "Predict",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
