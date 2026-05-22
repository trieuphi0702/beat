package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.translation.LanguageTranslationManager
import com.example.ui.viewmodel.DJViewModel

@Composable
fun LanguageSelectionScreen(viewModel: DJViewModel) {
    val selectedCode by viewModel.selectedLang.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.trim().isEmpty()) {
            LanguageTranslationManager.supportedLanguages
        } else {
            LanguageTranslationManager.supportedLanguages.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.nativeName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("language_selection_root")
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // Screen Heading Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Language,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = LanguageTranslationManager.getString("selector_lang"),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("language_heading_title")
            )
        }

        Text(
            text = "Select your preferred language interface to begin.",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Dynamic Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search 40+ languages...", color = Color.White.copy(alpha = 0.4f)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.08f),
                focusedContainerColor = Color(0xFF0F0F13),
                unfocusedContainerColor = Color(0xFF0F0F13)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("language_search_input")
        )

        // Languages grid scroll area
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 76.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .testTag("language_grid_view")
        ) {
            items(filteredLanguages, key = { it.code }) { lang ->
                val isSelected = lang.code == selectedCode
                val borderCol = if (isSelected) NeonPink else Color.White.copy(alpha = 0.05f)
                val bgCol = if (isSelected) Color(0xFF1A0A1F) else Color(0xFF0F0F13)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgCol)
                        .border(1.5.dp, borderCol, RoundedCornerShape(14.dp))
                        .clickable { viewModel.selectLanguage(lang.code) }
                        .padding(12.dp)
                        .testTag("lang_pills_${lang.code}")
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lang.flagEmoji,
                                fontSize = 22.sp,
                                modifier = Modifier.testTag("flag_field_${lang.code}")
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = NeonPink,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = lang.nativeName,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = lang.name,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Float navigation continuation CTA button
        Button(
            onClick = { viewModel.completeLanguageSelection() },
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("language_select_continue_button")
        ) {
            Text(
                text = "CONTINUE",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
