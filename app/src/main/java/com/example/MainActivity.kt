package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PaywallScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.DJViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DJViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = androidx.compose.animation.core.tween(400)) togetherWith
                                        fadeOut(animationSpec = androidx.compose.animation.core.tween(400))
                            },
                            label = "screen_routing_navigation"
                        ) { screen ->
                            when (screen) {
                                AppScreen.SPLASH -> SplashScreen(viewModel)
                                AppScreen.LANGUAGE_SELECT -> LanguageSelectionScreen(viewModel)
                                AppScreen.ONBOARDING -> OnboardingScreen(viewModel)
                                AppScreen.PAYWALL -> PaywallScreen(viewModel)
                                AppScreen.WORKSPACE -> WorkspaceScreen(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.synthEngine.release()
    }
}
