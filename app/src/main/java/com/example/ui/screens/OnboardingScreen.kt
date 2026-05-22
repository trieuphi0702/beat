package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonPink
import com.example.ui.translation.LanguageTranslationManager
import com.example.ui.viewmodel.DJViewModel

@Composable
fun OnboardingScreen(viewModel: DJViewModel) {
    val pageIndex by viewModel.onboardingPage.collectAsState()

    val pageTitle = when (pageIndex) {
        0 -> LanguageTranslationManager.getString("onboarding_1_title")
        1 -> LanguageTranslationManager.getString("onboarding_2_title")
        2 -> LanguageTranslationManager.getString("onboarding_3_title")
        else -> LanguageTranslationManager.getString("onboarding_4_title")
    }

    val pageDesc = when (pageIndex) {
        0 -> LanguageTranslationManager.getString("onboarding_1_desc")
        1 -> LanguageTranslationManager.getString("onboarding_2_desc")
        2 -> LanguageTranslationManager.getString("onboarding_3_desc")
        else -> LanguageTranslationManager.getString("onboarding_4_desc")
    }

    val systemIcon = when (pageIndex) {
        0 -> Icons.Filled.GraphicEq
        1 -> Icons.Filled.Equalizer
        2 -> Icons.Filled.RecordVoiceOver
        else -> Icons.Filled.MusicNote
    }

    val neonColor = when (pageIndex) {
        0 -> NeonCyan
        1 -> NeonPink
        2 -> NeonLime
        else -> NeonCyan
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("onboarding_screen_root")
    ) {
        // Skip header trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { viewModel.skipOnboarding() },
                modifier = Modifier.testTag("onboarding_skip_button")
            ) {
                Text(text = "SKIP", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Large Abstract Geometric illustration matching the active slide
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F0F13))
                .border(1.dp, neonColor.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = pageIndex, label = "vector_onboarding_illustration") { page ->
                when (page) {
                    0 -> { // Turntables
                        Canvas(modifier = Modifier.size(160.dp)) {
                            drawCircle(color = Color(0xFF1F2231), radius = size.minDimension / 2f)
                            drawCircle(color = Color.Black, radius = size.minDimension * 0.4f)
                            for (r in 1..4) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.08f),
                                    radius = size.minDimension * (0.08f * r + 0.1f),
                                    style = Stroke(width = 1.5f)
                                )
                            }
                            drawCircle(color = NeonCyan, radius = size.minDimension * 0.1f)
                        }
                    }
                    1 -> { // 3 Band EQ Graphic waves
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth().height(120.dp).padding(horizontal = 24.dp)
                        ) {
                            val heights = listOf(0.4f, 0.9f, 0.6f, 0.8f, 0.3f, 0.7f, 0.5f, 0.95f, 0.4f, 0.75f)
                            heights.forEachIndexed { idx, ht ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(110.dp * ht)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Brush.verticalGradient(listOf(NeonPink, NeonPink.copy(alpha = 0.2f))))
                                )
                            }
                        }
                    }
                    2 -> { // Stem separator nodes
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val stems = listOf("Vocals" to NeonPink, "Drums" to NeonLime, "Synth" to NeonCyan)
                            stems.forEach { stem ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(stem.second.copy(alpha = 0.15f))
                                            .border(1.5.dp, stem.second, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(systemIcon, contentDescription = null, tint = stem.second, modifier = Modifier.size(24.dp))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = stem.first, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                    else -> { // Sampler Pad grid
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.size(140.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                (0..1).forEach { _ ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonCyan.copy(alpha = 0.2f))
                                            .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                (0..1).forEach { _ ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonPink.copy(alpha = 0.15f))
                                            .border(1.5.dp, NeonPink, RoundedCornerShape(8.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.5f))

        // Icon category tag
        Icon(
            imageVector = systemIcon,
            contentDescription = null,
            tint = neonColor,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterHorizontally)
                .testTag("onboarding_illustration_sub_tag")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Page title
        Text(
            text = pageTitle,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_page_title"),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Page continuous description paragraph
        Text(
            text = pageDesc,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("onboarding_page_desc"),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Indicator progress dots representing active pages
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            (0..3).forEach { index ->
                val active = index == pageIndex
                val w = if (active) 24.dp else 8.dp
                val c = if (active) neonColor else Color.White.copy(alpha = 0.15f)

                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(8.dp)
                        .width(w)
                        .clip(CircleShape)
                        .background(c)
                )
            }
        }

        // Standard Navigation Step Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK
            IconButton(
                onClick = { viewModel.prevOnboarding() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .testTag("onboarding_back_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Back Step",
                    tint = Color.White
                )
            }

            // NEXT CTA
            Button(
                onClick = { viewModel.nextOnboarding() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = neonColor,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .width(160.dp)
                    .testTag("onboarding_next_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (pageIndex == 3) "PRO UNLOCK" else "NEXT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (pageIndex == 3) Icons.Filled.DoubleArrow else Icons.Filled.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
