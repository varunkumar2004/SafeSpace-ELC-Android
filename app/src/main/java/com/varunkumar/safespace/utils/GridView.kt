package com.varunkumar.safespace.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GridView(
    modifier: Modifier = Modifier,
    split: Float = 0.4f,
    bottomBackground: Color,
    topGridView: @Composable (Modifier) -> Unit,
    bottomGridView: @Composable (Modifier) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        topGridView(
            modifier
                .weight(split)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )

        val shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)

        bottomGridView(
            modifier.weight(1f - split)
                .clip(shape)
                .background(bottomBackground)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        )
    }
}