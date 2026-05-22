package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonLime
import com.example.ui.theme.NeonPink
import com.example.ui.translation.LanguageTranslationManager
import com.example.ui.viewmodel.DJViewModel

@Composable
fun PaywallScreen(viewModel: DJViewModel) {
    val context = LocalContext.current
    var isMonthlySelected by remember { mutableStateOf(true) }

    val coreBenefits = remember {
        listOf(
            LanguageTranslationManager.getString("paywall_benefit_1"),
            LanguageTranslationManager.getString("paywall_benefit_2"),
            LanguageTranslationManager.getString("paywall_benefit_3"),
            LanguageTranslationManager.getString("paywall_benefit_4")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("paywall_screen_root")
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Raw layout with top-right absolute-skip fader button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBackToWelcome() },
                modifier = Modifier.testTag("paywall_back_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }

            TextButton(
                onClick = { viewModel.skipPaywall() },
                modifier = Modifier.testTag("paywall_skip_button")
            ) {
                Text(
                    text = "SKIP & USE TRIAL",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Header Title
        Text(
            text = LanguageTranslationManager.getString("paywall_title").uppercase(),
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("paywall_title_text"),
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = LanguageTranslationManager.getString("paywall_desc"),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 13.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("paywall_desc_text"),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Premium Benefit Checklist
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F0F13))
                .border(1.dp, NeonCyan.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(16.dp)
                .testTag("benefits_container")
        ) {
            coreBenefits.forEachIndexed { i, benefit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Benefit Unlocked",
                        tint = NeonLime,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = benefit,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // High contrast Billing Toggle selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Plan 1: Monthly Subscription
            val mBorder = if (isMonthlySelected) NeonPink else Color.White.copy(alpha = 0.05f)
            val mBg = if (isMonthlySelected) Color(0xFF1D0F21) else Color(0xFF0F0F13)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(mBg)
                    .border(1.5.dp, mBorder, RoundedCornerShape(16.dp))
                    .clickable { isMonthlySelected = true }
                    .padding(16.dp)
                    .testTag("paywall_plan_monthly"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "MONTHLY", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$4.99", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "per month", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Cancel Anytime", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            // Plan 2: Lifetime Ownership
            val lBorder = if (!isMonthlySelected) NeonPink else Color.White.copy(alpha = 0.05f)
            val lBg = if (!isMonthlySelected) Color(0xFF1D0F21) else Color(0xFF0F0F13)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(lBg)
                    .border(1.5.dp, lBorder, RoundedCornerShape(16.dp))
                    .clickable { isMonthlySelected = false }
                    .padding(16.dp)
                    .testTag("paywall_plan_lifetime"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(NeonLime, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "BEST VALUE", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$19.99", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "one-time pay", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pay Once, Keep Forever", color = NeonLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Professional Feedback reviews
        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F13)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = ElectricYellow, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "BeatMix DJ Review", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"This offline PCM synth feels faster & cleaner than standard streaming applications I've used. Absolute monster for creating instant drum grooves on flights!\"",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Secondary CTA: Unlocks Premium Mode!
        Button(
            onClick = {
                viewModel.purchasePremiumSuccess()
                Toast.makeText(context, "Welcome to Premium Studio! All Features Unlocked.", Toast.LENGTH_LONG).show()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonPink,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("paywall_unlock_premium_button")
        ) {
            Text(
                text = if (isMonthlySelected) LanguageTranslationManager.getString("unlock_now").uppercase()
                else LanguageTranslationManager.getString("unlock_lifetime").uppercase(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Restore purchasing state
        TextButton(
            onClick = {
                viewModel.restorePurchases()
                Toast.makeText(context, "Previous Premium Purchases Restored Successfully.", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("paywall_restore_button")
        ) {
            Text(
                text = "Restore Existing Subscription",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Offline security notice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.OfflinePin,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = LanguageTranslationManager.getString("premium_secured"),
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
