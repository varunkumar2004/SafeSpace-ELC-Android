package com.varunkumar.safespace.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.varunkumar.safespace.shared.UserData

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    userData: UserData?,
    onSignOut: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            userData?.profilePictureUrl?.let { picture ->
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    model = picture,
                    contentDescription = "profile image",
                    contentScale = ContentScale.Crop
                )
            }

            userData?.username?.let {
                Text(
                    text = it,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(onClick = onSignOut) {
            Icon(
                tint = Color.Black,
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null
            )
        }
    }
}