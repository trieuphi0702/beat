package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Explicit
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonPink
import com.example.ui.translation.LanguageTranslationManager
import com.example.ui.viewmodel.DJViewModel
import kotlin.math.sin

@Composable
fun WorkspaceScreen(viewModel: DJViewModel) {
    val context = LocalContext.current
    val isPremium by viewModel.isPremium.collectAsState()

    // Retrieve reactive audio states
    val isPlayingA by viewModel.synthEngine.isPlayingA.collectAsState()
    val isPlayingB by viewModel.synthEngine.isPlayingB.collectAsState()

    val bpmA by viewModel.synthEngine.bpmA.collectAsState()
    val bpmB by viewModel.synthEngine.bpmB.collectAsState()

    val pitchA by viewModel.synthEngine.pitchA.collectAsState()
    val pitchB by viewModel.synthEngine.pitchB.collectAsState()

    val crossfader by viewModel.synthEngine.crossfader.collectAsState()

    val activeFx by viewModel.synthEngine.activeFx.collectAsState()

    val stemVocals by viewModel.synthEngine.stemVocals.collectAsState()
    val stemDrums by viewModel.synthEngine.stemDrums.collectAsState()
    val stemInstruments by viewModel.synthEngine.stemInstruments.collectAsState()

    val isRecording by viewModel.synthEngine.isRecording.collectAsState()
    val recordDurationText by viewModel.recordDurationText.collectAsState()

    // Saved database parameters
    val cuePointsDeckA by viewModel.cuePointsDeckA.collectAsState()
    val cuePointsDeckB by viewModel.cuePointsDeckB.collectAsState()
    val recordedMixes by viewModel.recordedMixes.collectAsState()

    // 3-Band Volume Equalizers
    val eqLowA by viewModel.synthEngine.eqLowA.collectAsState()
    val eqMidA by viewModel.synthEngine.eqMidA.collectAsState()
    val eqHighA by viewModel.synthEngine.eqHighA.collectAsState()

    val eqLowB by viewModel.synthEngine.eqLowB.collectAsState()
    val eqMidB by viewModel.synthEngine.eqMidB.collectAsState()
    val eqHighB by viewModel.synthEngine.eqHighB.collectAsState()

    // Double LP spinning transition
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_spin_transition")
    val spinAngleA by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlayingA) 2000 else 10000000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_a"
    )

    val spinAngleB by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlayingB) 1800 else 10000000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_b"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("workspace_screen_root"),
        contentPadding = PaddingValues(top = 36.dp, bottom = 24.dp, start = 12.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. Session Header Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111422)),
                border = BorderStrokeWrap(1.dp, NeonCyan.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "STUDIO LIVE",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isPremium) {
                                Box(
                                    modifier = Modifier
                                        .background(NeonPink, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "PRO", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = if (isRecording) "RECORDING ON-THE-FLY" else "BEATMIX DJ STATION",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Recording controller buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isRecording) {
                            Text(
                                text = recordDurationText,
                                color = NeonPink,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("recording_clock_text")
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleLiveRecording() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isRecording) NeonPink else Color.White.copy(alpha = 0.08f))
                                .size(42.dp)
                                .testTag("recording_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FiberManualRecord,
                                contentDescription = "Toggle Live WAV record",
                                tint = if (isRecording) Color.White else NeonPink,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Animated Dual Waveform Multiplexer Overlay (Immersive UI element)
        item {
            DualWaveformsVisualizer(
                isPlayingA = isPlayingA,
                isPlayingB = isPlayingB,
                bpmA = bpmA,
                bpmB = bpmB
            )
        }

        // 2. Dual Virtual Turntables Row (Interactive Decks)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // DECK A
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F0F13))
                        .border(1.dp, if (isPlayingA) NeonCyan else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Text(text = "DECK A", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Electro Groove", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${bpmA} BPM", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vinyl Canvas with Touch gestures
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.CenterHorizontally)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    // Scratch trigger values
                                    viewModel.synthEngine.triggerScratchFX("DECK_A", dragAmount.x / 10f)
                                }
                            }
                            .testTag("vinyl_deck_a"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(spinAngleA)
                        ) {
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val radius = size.minDimension / 2f

                            // Turntable outer rim
                            drawCircle(color = Color(0xFF08080C), radius = radius)
                            drawCircle(color = Color.Black, radius = radius * 0.96f)

                            // Grooves
                            for (r in 1..5) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.06f),
                                    radius = radius * (0.13f * r + 0.13f),
                                    style = Stroke(width = 1f)
                                )
                            }
                            drawCircle(color = NeonCyan.copy(alpha = 0.15f), radius = radius * 0.32f)
                            drawCircle(color = NeonCyan, radius = radius * 0.26f)
                            drawCircle(color = Color.Black, radius = radius * 0.12f)
                            drawCircle(color = Color.White, radius = radius * 0.04f)

                            // Solid visual pitch needle-guide marker line that rotates dynamically
                            drawLine(
                                color = NeonCyan,
                                start = Offset(centerX, centerY - radius * 0.28f),
                                end = Offset(centerX, centerY - radius * 0.95f),
                                strokeWidth = 5f
                            )
                        }

                        // Hot Cues indicator lines
                        cuePointsDeckA.forEach { cue ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .offset(x = (25 * sin(cue.positionPercent * 6.28)).dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(cue.colorHex)))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pitch Slider A
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Filled.FlashOn, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "PITCH: ${String.format("%.1f", pitchA)}x", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                    }
                    Slider(
                        value = pitchA,
                        onValueChange = { viewModel.synthEngine.setPitchA(it) },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan.copy(alpha = 0.6f),
                            inactiveTrackColor = Color.White.copy(alpha = 0.05f)
                        ),
                        modifier = Modifier
                            .height(24.dp)
                            .testTag("pitch_slider_a")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Buttons deck actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.synthEngine.togglePlayA() },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                                .testTag("play_button_a")
                        ) {
                            Icon(
                                imageVector = if (isPlayingA) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play Deck A",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.addCuePoint("DECK_A", pitchA) },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                                .testTag("cue_button_a")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = "Save Hotcue Deck A",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Hotcues listing
                    if (cuePointsDeckA.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(cuePointsDeckA, key = { it.id }) { cue ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF18181F))
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            viewModel.synthEngine.triggerScratchFX("DECK_A", 1.5f)
                                            Toast.makeText(context, "Jumped to Hotcue ${cue.positionLabel}", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(cue.colorHex))))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = cue.positionLabel, color = Color.White, fontSize = 9.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = null,
                                            tint = Color.Red,
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clickable { viewModel.deleteCuePoint(cue.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // DECK B
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F0F13))
                        .border(1.dp, if (isPlayingB) NeonPink else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Text(text = "DECK B", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Neon Horizon", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${bpmB} BPM", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vinyl Canvas with Touch gestures
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .align(Alignment.CenterHorizontally)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    viewModel.synthEngine.triggerScratchFX("DECK_B", dragAmount.x / 10f)
                                }
                            }
                            .testTag("vinyl_deck_b"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(spinAngleB)
                        ) {
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val radius = size.minDimension / 2f

                            // Turntable outer rim
                            drawCircle(color = Color(0xFF08080C), radius = radius)
                            drawCircle(color = Color.Black, radius = radius * 0.96f)

                            // Grooves
                            for (r in 1..5) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.06f),
                                    radius = radius * (0.13f * r + 0.13f),
                                    style = Stroke(width = 1f)
                                )
                            }
                            drawCircle(color = NeonPink.copy(alpha = 0.15f), radius = radius * 0.32f)
                            drawCircle(color = NeonPink, radius = radius * 0.26f)
                            drawCircle(color = Color.Black, radius = radius * 0.12f)
                            drawCircle(color = Color.White, radius = radius * 0.04f)

                            // Solid visual pitch needle-guide marker line that rotates dynamically
                            drawLine(
                                color = NeonPink,
                                start = Offset(centerX, centerY - radius * 0.28f),
                                end = Offset(centerX, centerY - radius * 0.95f),
                                strokeWidth = 5f
                            )
                        }

                        cuePointsDeckB.forEach { cue ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .offset(x = (25 * sin(cue.positionPercent * 6.28)).dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(cue.colorHex)))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pitch Slider B
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Filled.FlashOn, contentDescription = null, tint = NeonPink, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "PITCH: ${String.format("%.1f", pitchB)}x", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                    }
                    Slider(
                        value = pitchB,
                        onValueChange = { viewModel.synthEngine.setPitchB(it) },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPink,
                            activeTrackColor = NeonPink.copy(alpha = 0.6f),
                            inactiveTrackColor = Color.White.copy(alpha = 0.05f)
                        ),
                        modifier = Modifier
                            .height(24.dp)
                            .testTag("pitch_slider_b")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.synthEngine.togglePlayB() },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NeonPink)
                                .testTag("play_button_b")
                        ) {
                            Icon(
                                imageVector = if (isPlayingB) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "Play Deck B",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.addCuePoint("DECK_B", pitchB) },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                                .testTag("cue_button_b")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = "Save Hotcue Deck B",
                                tint = NeonPink,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (cuePointsDeckB.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            items(cuePointsDeckB, key = { it.id }) { cue ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF18181F))
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            viewModel.synthEngine.triggerScratchFX("DECK_B", 1.5f)
                                            Toast.makeText(context, "Jumped to Hotcue ${cue.positionLabel}", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(cue.colorHex))))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = cue.positionLabel, color = Color.White, fontSize = 9.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = null,
                                            tint = Color.Red,
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clickable { viewModel.deleteCuePoint(cue.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Smart Sync Engine Panel
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF10121B))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .clickable {
                        viewModel.synthEngine.syncBpm()
                        Toast.makeText(context, "Smart Tempo Synced perfectly to $bpmA BPM!", Toast.LENGTH_SHORT).show()
                    }
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .testTag("bpm_sync_row"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Sync, contentDescription = null, tint = NeonLime, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SMART TEMPO SYNC (" + bpmA + " ⇄ " + bpmB + ")",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // 4. 3-Band Equalizer Controllers (Low, Mid, High for both Decks)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10121B)),
                border = BorderStrokeWrap(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "3-BAND FREQUENCY EQ MASTER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // EQ Deck A
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "DECK A FILTER", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            // High
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "HI", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, modifier = Modifier.width(24.dp))
                                Slider(
                                    value = eqHighA,
                                    onValueChange = { viewModel.synthEngine.setEqHighA(it) },
                                    valueRange = 0.0f..2.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                            // Mid
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "MID", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, modifier = Modifier.width(24.dp))
                                Slider(
                                    value = eqMidA,
                                    onValueChange = { viewModel.synthEngine.setEqMidA(it) },
                                    valueRange = 0.0f..2.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                            // Bass
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "LOW", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, modifier = Modifier.width(24.dp))
                                Slider(
                                    value = eqLowA,
                                    onValueChange = { viewModel.synthEngine.setEqLowA(it) },
                                    valueRange = 0.0f..2.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }

                        // EQ Deck B
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "DECK B FILTER", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            // High
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "HI", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, modifier = Modifier.width(24.dp))
                                Slider(
                                    value = eqHighB,
                                    onValueChange = { viewModel.synthEngine.setEqHighB(it) },
                                    valueRange = 0.0f..2.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                            // Mid
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "MID", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, modifier = Modifier.width(24.dp))
                                Slider(
                                    value = eqMidB,
                                    onValueChange = { viewModel.synthEngine.setEqMidB(it) },
                                    valueRange = 0.0f..2.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                            // Bass
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "LOW", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, modifier = Modifier.width(24.dp))
                                Slider(
                                    value = eqLowB,
                                    onValueChange = { viewModel.synthEngine.setEqLowB(it) },
                                    valueRange = 0.0f..2.0f,
                                    colors = SliderDefaults.colors(thumbColor = NeonPink, activeTrackColor = NeonPink),
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. Crossfader Slider Slider Section in Equalizers panel
                    Text(
                        text = "DECK A ⇄ CROSSFADER ⇄ DECK B",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Filled.VolumeOff, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Slider(
                            value = crossfader,
                            onValueChange = { viewModel.synthEngine.setCrossfader(it) },
                            valueRange = 0.0f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonLime,
                                activeTrackColor = NeonLime.copy(alpha = 0.7f),
                                inactiveTrackColor = Color.White.copy(alpha = 0.05f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                                .testTag("crossfader_slider")
                        )
                        Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 5. Interactive FX Sound Modifiers Selection Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF10121B))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "HARDWARE STUDIO FX SELECTION",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val fxList = listOf("Flanger", "Phaser", "Echo", "Reverb", "Filter")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    fxList.forEach { fxName ->
                        val selected = activeFx == fxName
                        val bColor = if (selected) NeonPink else Color.White.copy(alpha = 0.05f)
                        val txtColor = if (selected) Color.White else Color.White.copy(alpha = 0.6f)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) Color(0xFF2E1225) else Color(0xFF161927))
                                .border(1.dp, bColor, RoundedCornerShape(10.dp))
                                .clickable { viewModel.synthEngine.toggleFx(fxName) }
                                .padding(vertical = 8.dp)
                                .testTag("fx_button_$fxName"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fxName.uppercase(),
                                color = txtColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 6. On-Device AI Stem Separator Panel simulation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF10121B)),
                border = BorderStrokeWrap(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ON-DEVICE AI STEMS SEPARATION",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(NeonLime.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "OFFLINE AI", color = NeonLime, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "Separate vocals, drums, or backing tracks dynamically with low-latency synthetic filters.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Stem 1: Vocals
                        val vColor = if (stemVocals) NeonPink else Color.White.copy(alpha = 0.05f)
                        val vBg = if (stemVocals) Color(0xFF2E1225) else Color(0xFF161927)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(vBg)
                                .border(1.dp, vColor, RoundedCornerShape(10.dp))
                                .clickable { viewModel.synthEngine.toggleStemVocals() }
                                .padding(8.dp)
                                .testTag("stem_vocals_toggle"),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "VOCALS", color = if (stemVocals) Color.White else Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            // Waveform animation helper
                            Row(modifier = Modifier.height(16.dp), horizontalArrangement = Arrangement.Center) {
                                repeat(5) { i ->
                                    val sizeVal = if (stemVocals && (isPlayingA || isPlayingB)) (10..16).random().dp else 4.dp
                                    Box(modifier = Modifier.padding(horizontal = 1.dp).width(2.dp).height(sizeVal).background(NeonPink))
                                }
                            }
                        }

                        // Stem 2: Drums
                        val dColor = if (stemDrums) NeonLime else Color.White.copy(alpha = 0.05f)
                        val dBg = if (stemDrums) Color(0xFF122E1A) else Color(0xFF161927)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(dBg)
                                .border(1.dp, dColor, RoundedCornerShape(10.dp))
                                .clickable { viewModel.synthEngine.toggleStemDrums() }
                                .padding(8.dp)
                                .testTag("stem_drums_toggle"),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "DRUMS", color = if (stemDrums) Color.White else Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.height(16.dp), horizontalArrangement = Arrangement.Center) {
                                repeat(5) { i ->
                                    val sizeVal = if (stemDrums && (isPlayingA || isPlayingB)) (10..16).random().dp else 4.dp
                                    Box(modifier = Modifier.padding(horizontal = 1.dp).width(2.dp).height(sizeVal).background(NeonLime))
                                }
                            }
                        }

                        // Stem 3: Instruments
                        val iColor = if (stemInstruments) NeonCyan else Color.White.copy(alpha = 0.05f)
                        val iBg = if (stemInstruments) Color(0xFF122A2E) else Color(0xFF161927)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(iBg)
                                .border(1.dp, iColor, RoundedCornerShape(10.dp))
                                .clickable { viewModel.synthEngine.toggleStemInstruments() }
                                .padding(8.dp)
                                .testTag("stem_instruments_toggle"),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "SYNTHS", color = if (stemInstruments) Color.White else Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.height(16.dp), horizontalArrangement = Arrangement.Center) {
                                repeat(5) { i ->
                                    val sizeVal = if (stemInstruments && (isPlayingA || isPlayingB)) (10..16).random().dp else 4.dp
                                    Box(modifier = Modifier.padding(horizontal = 1.dp).width(2.dp).height(sizeVal).background(NeonCyan))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Interactive 8 Drum Pads Sampler Matrix
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF10121B))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "HARDWARE SAMPLER DRUM PADS",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap any pad to trigger low-latency synthetic live instruments.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                val pads = listOf(
                    "KICK" to NeonCyan, "SNARE" to NeonPink,
                    "HI-HAT" to NeonLime, "CLAP" to ElectricYellow,
                    "LASER" to NeonPink, "SCI-FI" to NeonCyan,
                    "RISER" to NeonLime, "AIRHORN" to ElectricYellow
                )

                // Grid layout drawing 8 pads
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (idx in 0..3) {
                            val pad = pads[idx]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(pad.second.copy(alpha = 0.12f))
                                    .border(1.dp, pad.second.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.synthEngine.triggerDrumPad(idx) }
                                    .padding(4.dp)
                                    .testTag("sampler_pad_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pad.first,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (idx in 4..7) {
                            val pad = pads[idx]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(pad.second.copy(alpha = 0.12f))
                                    .border(1.dp, pad.second.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.synthEngine.triggerDrumPad(idx) }
                                    .padding(4.dp)
                                    .testTag("sampler_pad_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pad.first,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. Recorded Session Studio Gallery Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF10121B))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MY RECORDED MIX SESSIONS",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${recordedMixes.size} saved",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (recordedMixes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Filled.MusicNote, contentDescription = null, tint = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Start recording above to save your first DJ live mix offline!",
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recordedMixes.forEach { mix ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF161927))
                                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                                    .testTag("recorded_mix_card_${mix.id}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Equalizer,
                                    contentDescription = null,
                                    tint = NeonPink,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = mix.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${mix.durationSeconds}s duration  •  ${String.format("%.1f", mix.fileSizeBytes / 1024f)} KB",
                                        color = Color.White.copy(alpha = 0.4f),
                                        fontSize = 10.sp
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            viewModel.synthEngine.triggerScratchFX("DECK_A", 1.2f)
                                            Toast.makeText(context, "Playing saved mix: ${mix.title}!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Play Mix", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "WAV Location: ${mix.filePath}", Toast.LENGTH_LONG).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Filled.Share, contentDescription = "Share Location", tint = NeonLime, modifier = Modifier.size(16.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteMix(mix.id, mix.filePath)
                                            Toast.makeText(context, "Recording deleted from SQLite database", Toast.LENGTH_LONG).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Mix", tint = Color.Red, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Compact helper to avoid standard library border issues
@Composable
fun BorderStrokeWrap(width: androidx.compose.ui.unit.Dp, brush: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(width, brush)
}

@Composable
fun DualWaveformsVisualizer(
    isPlayingA: Boolean,
    isPlayingB: Boolean,
    bpmA: Int,
    bpmB: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    val phaseA by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlayingA) 1200 else 1000000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_a"
    )
    val phaseB by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlayingB) 1000 else 1000000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_b"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F13)),
        border = BorderStrokeWrap(1.dp, Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Label Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STUDIO WAVEFORM MULTIPLEXER",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isPlayingA || isPlayingB) Color(0xFF4ADE80) else Color(0xFF71717A))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlayingA || isPlayingB) "ONLINE" else "STANDBY",
                        color = if (isPlayingA || isPlayingB) Color(0xFF4ADE80) else Color(0xFF71717A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Graph visualizers
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Deck A Waveform
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(45) { index ->
                        val baseHeight = 6f + (kotlin.math.sin(index * 0.4f + phaseA) * 12f).coerceAtLeast(0f)
                        val randomVariance = if (isPlayingA) (0..6).random() else 0
                        val heightVal = (baseHeight + randomVariance).coerceIn(4f, 26f).dp
                        val color = if (isPlayingA) NeonCyan else Color.White.copy(alpha = 0.15f)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(heightVal)
                                .background(color, RoundedCornerShape(1.dp))
                        )
                    }
                }

                // Deck B Waveform
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(45) { index ->
                        val baseHeight = 6f + (kotlin.math.cos(index * 0.35f + phaseB) * 12f).coerceAtLeast(0f)
                        val randomVariance = if (isPlayingB) (0..6).random() else 0
                        val heightVal = (baseHeight + randomVariance).coerceIn(4f, 26f).dp
                        val color = if (isPlayingB) NeonPink else Color.White.copy(alpha = 0.15f)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(heightVal)
                                .background(color, RoundedCornerShape(1.dp))
                        )
                    }
                }
            }
        }
    }
}
