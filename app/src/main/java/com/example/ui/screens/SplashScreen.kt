package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.viewmodel.DJViewModel

@Composable
fun SplashScreen(viewModel: DJViewModel) {
    val status by viewModel.sdkInitStatus.collectAsState()
    val progress by viewModel.sdkInitProgress.collectAsState()

    // Single source spinning infinite animation representing vinyl deck
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_loading")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("splash_screen_root"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Large DJ Logo Graphic
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonPink.copy(alpha = 0.3f), Color.Transparent),
                        radius = 240f
                    )
                )
                .border(2.dp, NeonCyan, CircleShape)
        ) {
            // Spinning Vinyl disc simulation
            Canvas(
                modifier = Modifier
                    .size(140.dp)
                    .rotate(rotationAngle)
            ) {
                val center = this.size / 2f
                val radius = this.size.minDimension / 2f

                // Flat disc background
                drawCircle(color = Color(0xFF11131A), radius = radius)

                // Groove circles representing physical disc tracts
                drawCircle(color = Color.Black.copy(alpha = 0.9f), radius = radius * 0.9f, style = Stroke(width = 1f))
                drawCircle(color = Color.Gray.copy(alpha = 0.4f), radius = radius * 0.8f, style = Stroke(width = 2f))
                drawCircle(color = Color.Black.copy(alpha = 0.9f), radius = radius * 0.7f, style = Stroke(width = 1f))
                drawCircle(color = Color.Gray.copy(alpha = 0.4f), radius = radius * 0.6f, style = Stroke(width = 2f))
                drawCircle(color = Color.Black.copy(alpha = 0.9f), radius = radius * 0.5f, style = Stroke(width = 1f))

                // Center Label
                drawCircle(color = NeonPink, radius = radius * 0.25f)
                drawCircle(color = Color.Black, radius = radius * 0.08f)
            }

            Icon(
                imageVector = Icons.Filled.GraphicEq,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // App Brand Taglines
        Text(
            text = "BEATMIX DJ",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            modifier = Modifier.testTag("app_brand_title")
        )

        Text(
            text = "AI MUSIC SYNTHESIZER",
            color = NeonCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 4.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Interactive status loads
        CircularProgressIndicator(
            progress = { progress },
            color = NeonPink,
            strokeWidth = 3.dp,
            modifier = Modifier
                .size(48.dp)
                .testTag("splash_progress_bar")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = status,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally)
                .testTag("sdk_init_status_text"),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
