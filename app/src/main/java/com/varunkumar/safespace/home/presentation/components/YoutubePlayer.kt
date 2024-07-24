package com.varunkumar.safespace.home.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YoutubePlayer(
    youtubeVideoUrl: String,
    lifecycleOwner: LifecycleOwner
) {
    val youtubeId = extractYoutubeVideoId(youtubeVideoUrl)

    youtubeId?.let {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp)),
            factory = { context ->
                YouTubePlayerView(context = context).apply {
                    lifecycleOwner.lifecycle.addObserver(this)

                    addYouTubePlayerListener(
                        object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.cueVideo(youtubeId, 0f)
                            }
                        }
                    )
                }
            }
        )
    }
}

private fun extractYoutubeVideoId(url: String?): String? {
    val regex = """^(?:https?://)?(?:(?:www\.)?|m\.)?(?:youtu\.be/|watch\?v=)([^#&?]+)"""
        .toRegex(RegexOption.MULTILINE)
    val match = regex.find(url ?: "") ?: return null
    return match.groupValues[1]
}