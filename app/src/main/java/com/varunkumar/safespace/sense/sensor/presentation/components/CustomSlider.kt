package com.varunkumar.safespace.sense.sensor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.varunkumar.safespace.sense.sensor.data.HealthSensors
import com.varunkumar.safespace.sense.sensor.presentation.StressDetectionViewModel
import com.varunkumar.safespace.ui.theme.primary
import com.varunkumar.safespace.ui.theme.surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    modifier: Modifier = Modifier,
    viewModel: StressDetectionViewModel,
    sliderPosition: Float,
    sensor: HealthSensors
) {
    val height = TextFieldDefaults.MinHeight - 5.dp
    val shape = RoundedCornerShape(40.dp)
    val isSleep = sensor is HealthSensors.HoursOfSleepSensors

    Column(
        modifier = modifier
    ) {
        Column(
            modifier = modifier
                .clip(shape)
                .background(primary)
                .padding(5.dp)
                .height(height),
        ) {
            Slider(
                modifier = modifier,
                value = sliderPosition,
                onValueChange = { value ->
                    viewModel.sliderChange(value, sensor)
                },
                valueRange = sensor.low..sensor.high,
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent
                ),
                thumb = {
                    ElevatedCard(
                        modifier = Modifier
                            .size(height),
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 5.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = surface
                        ),
                        content = {
                            if (isSleep) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = sliderPosition.toInt().toString(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    )
                },
                track = {
                    Row(
                        modifier = modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!isSleep) {
                            Text("Slow")
                        }

                        Text(
                            text = sensor.label,
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (!isSleep) {
                            Text("Fast")
                        }
                    }
                }
            )
        }
    }
}