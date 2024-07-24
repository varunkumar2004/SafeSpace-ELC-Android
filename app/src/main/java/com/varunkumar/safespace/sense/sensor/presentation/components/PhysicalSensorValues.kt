package com.varunkumar.safespace.sense.sensor.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.varunkumar.safespace.shared.SensorDataResponse
import com.varunkumar.safespace.ui.theme.secondary

@Composable
fun PhysicalSensorValues(
    modifier: Modifier = Modifier,
    response: SensorDataResponse
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val sensorModifier = Modifier
                .weight(100 / 3f)
                .clip(RoundedCornerShape(20.dp))
                .background(secondary)
                .padding(10.dp)

            SensorBox(
                modifier = sensorModifier,
                key = "Heart Rate",
                value = response.heartRate.toString()
            )

            SensorBox(
                modifier = sensorModifier,
                key = "SPO2",
                value = response.spO2.toString()
            )

            SensorBox(
                modifier = sensorModifier,
                key = "Temperature",
                value = response.temperature.toString()
            )
        }
    }
}

@Composable
fun SensorBox(
    modifier: Modifier = Modifier,
    key: String,
    value: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(text = key, style = MaterialTheme.typography.bodySmall)
    }
}

