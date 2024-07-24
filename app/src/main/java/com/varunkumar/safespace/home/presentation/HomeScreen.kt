package com.varunkumar.safespace.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.varunkumar.safespace.home.presentation.components.VideoCarousel
import com.varunkumar.safespace.home.presentation.components.ProfileScreen
import com.varunkumar.safespace.shared.UserData
import com.varunkumar.safespace.ui.theme.primary
import com.varunkumar.safespace.ui.theme.secondary
import com.varunkumar.safespace.utils.GridView
import com.varunkumar.safespace.utils.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userData: UserData?,
    viewModel: HomeViewModel,
    onChatNavigate: () -> Unit,
    onSenseNavigate: () -> Unit,
    onSignOut: () -> Unit
) {
    val fModifier = Modifier.fillMaxWidth()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember {
        mutableStateOf(false)
    }

    Scaffold(
        containerColor = primary,
        topBar = {
            TopAppBar(
                modifier = fModifier,
                userData = userData,
                onChatNavigate = onChatNavigate,
                onSheetChange = { isSheetOpen = true }
            )
        }
    ) {
        Column(
            modifier = modifier
                .padding(it),
        ) {
            GridView(
                modifier = modifier,
                bottomBackground = secondary,
                split = 0.5f,
                topGridView = { modifier ->
                    val stress = state.stressLevel

                    ElevatedButton(
                        modifier = modifier,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = stress?.let { level ->
                                extractStressLevel(level).second
                            } ?: secondary,
                            contentColor = Color.Black
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp
                        ),
                        onClick = onSenseNavigate,
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stress?.let { level ->
                                extractStressLevel(level).first + " Stress"
                            } ?: "Scan"
                        )
                    }
                },
                bottomGridView = { modifier ->
                    state.recommendedVideos?.let { response ->
                        Box(
                            modifier = modifier,
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                modifier = fModifier.align(Alignment.TopCenter),
                                textAlign = TextAlign.Center,
                                text = "Recommendations",
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            VideoCarousel(
                                videos = response
                            )
                        }
                    }
                }
            )
        }
    }

    if (isSheetOpen) {
        ModalBottomSheet(
            containerColor = primary,
            contentColor = Color.Black,
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false }
        ) {
            ProfileScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                userData = userData,
                onSignOut = onSignOut
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    modifier: Modifier = Modifier,
    userData: UserData?,
    onChatNavigate: () -> Unit,
    onSheetChange: () -> Unit,
) {
    androidx.compose.material3.TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Black
        ),
        title = {
            Text(
                text = "Home",
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                IconButton(onClick = onChatNavigate) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Message,
                        contentDescription = null
                    )
                }

                userData?.profilePictureUrl?.let {
                    AsyncImage(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable { onSheetChange() },
                        model = userData.profilePictureUrl,
                        contentDescription = "Profile",
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    )
}

private fun extractStressLevel(level: String): Pair<String, Color> {
    val stressLevel = level.trim().removeSurrounding("[", "]").toInt()

    val colors = listOf(
        Color(0xFFB2E8FF),
        Color(0xFFA4D0FF),
        Color(0xFFE1A5FF),
        Color(0xFFFFA3B3),
        Color(0xFFFF9191)
    )

    val levelName = arrayOf("Very Low", "Low", "Medium", "High", "Very High")
    return Pair(levelName[stressLevel], colors[stressLevel])
}
