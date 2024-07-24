package com.varunkumar.safespace.home.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.varunkumar.safespace.ui.theme.primary

@Composable
fun VideoCarousel(
    modifier: Modifier = Modifier,
    videos: List<String>
) {
    var index by remember { mutableIntStateOf(0) }
    val lifeCycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = Color.Black
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                enabled = index > 0,
                colors = buttonColors,
                onClick = { if (index > 0) index-- },
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                colors = buttonColors,
                enabled = index < videos.size - 1,
                onClick = { if (index < videos.size) index++ },
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        AnimatedContent(targetState = index, label = "current video") { index ->
            YoutubePlayer(
                youtubeVideoUrl = videos[index],
                lifecycleOwner = lifeCycleOwner
            )
        }
    }
}