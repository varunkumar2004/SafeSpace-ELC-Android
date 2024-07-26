package com.varunkumar.safespace.chat.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.varunkumar.safespace.R
import com.varunkumar.safespace.chat.data.ChatMessage
import com.varunkumar.safespace.ui.theme.primary
import com.varunkumar.safespace.ui.theme.secondary
import com.varunkumar.safespace.utils.GridView
import com.varunkumar.safespace.utils.Result
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit
) {
    val viewModel = hiltViewModel<ChatViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val shape = RoundedCornerShape(30.dp)
    val fModifier = Modifier.fillMaxWidth()
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = primary,
        topBar = {
            TopAppBar(
                modifier = fModifier,
                onBackButtonClick = onBackButtonClick
            )
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
                    SpeechAnimationBox(
                        modifier = modifier,
                        isPlaying = state.speakText != null
                    )
                },
                bottomGridView = { modifier ->
                    Column(modifier = modifier) {
                        TextField(
                            modifier = fModifier,
                            value = state.message,
                            onValueChange = { value ->
                                viewModel.onMessageChange(value)
                            },
                            shape = shape,
                            colors = TextFieldDefaults.colors(
                                cursorColor = Color.Black,
                                focusedContainerColor = primary,
                                unfocusedContainerColor = primary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions {
                                viewModel.sendPrompt()
                                keyboardController?.hide()
                            },
                            placeholder = { Text(text = "Prompt") },
                            maxLines = 1,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (state.message.isNotEmpty() || state.message.isNotBlank()) {
                                            viewModel.sendPrompt()
                                            keyboardController?.hide()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = null
                                    )
                                }
                            }
                        )

                        LazyColumn(
                            modifier = fModifier,
                            state = listState,
                        ) {
                            items(state.messages) { msg ->
                                ChatItemMessage(
                                    modifier = fModifier,
                                    message = msg
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SpeechAnimationBox(
    modifier: Modifier = Modifier,
    isPlaying: Boolean
) {
    val composition by
    rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.message_animation))

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = isPlaying,
            contentAlignment = Alignment.Center,
            label = ""
        ) { playing ->
            if (playing) {
                LottieAnimation(
                    modifier = Modifier.size(300.dp),
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color(0xffabc1ff))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Black
        ),
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = { Text(text = "Chat", fontWeight = FontWeight.Bold) }
    )
}

@Composable
fun ChatItemMessage(
    modifier: Modifier = Modifier,
    message: ChatMessage
) {
    val shape = RoundedCornerShape(30.dp)
    val painterImage = painterResource(
        id = if (message.isBot) R.drawable.ai_illus
        else R.drawable.squiggle
    )

    ListItem(
        modifier = modifier.clip(shape),
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        leadingContent = {
            Image(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape),
                painter = painterImage,
                contentDescription = null
            )
        },
        trailingContent = {
            if (!message.isBot) {
                Text(
                    text = extractTimeFromTimestamp(message.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        headlineContent = {
            Text(
                text = message.data,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    )
}

private fun extractTimeFromTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("hh:mm a", java.util.Locale.US)
    return dateFormat.format(date)
}
