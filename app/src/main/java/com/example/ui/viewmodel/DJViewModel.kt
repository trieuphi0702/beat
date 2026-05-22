package com.example.ui.viewmodel

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.DJSynthEngine
import com.example.audio.RecordResult
import com.example.data.database.AppDatabase
import com.example.data.database.CuePoint
import com.example.data.database.MixRepository
import com.example.data.database.RecordedMix
import com.example.ui.translation.LanguageTranslationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class AppScreen {
    SPLASH,
    LANGUAGE_SELECT,
    ONBOARDING,
    PAYWALL,
    WORKSPACE
}

class DJViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = MixRepository(database.mixDao())

    val synthEngine = DJSynthEngine()

    // App flow state
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen = _currentScreen.asStateFlow()

    // Onboarding progress
    private val _onboardingPage = MutableStateFlow(0)
    val onboardingPage = _onboardingPage.asStateFlow()

    // SDK Init state for Splash Screen
    private val _sdkInitStatus = MutableStateFlow("Initializing system...")
    val sdkInitStatus = _sdkInitStatus.asStateFlow()

    private val _sdkInitProgress = MutableStateFlow(0f)
    val sdkInitProgress = _sdkInitProgress.asStateFlow()

    // Premium validation State
    private val _isPremium = MutableStateFlow(false)
    val isPremium = _isPremium.asStateFlow()

    // Translation State helper
    private val _selectedLang = MutableStateFlow("en")
    val selectedLang = _selectedLang.asStateFlow()

    // Reactive streams from Database
    val recordedMixes = repository.allMixes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cuePointsDeckA = repository.getCuePointsForDeck("DECK_A").stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cuePointsDeckB = repository.getCuePointsForDeck("DECK_B").stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current record elapsed time counters
    private val _recordDurationText = MutableStateFlow("00:00")
    val recordDurationText = _recordDurationText.asStateFlow()
    private var recordTimerJob: kotlinx.coroutines.Job? = null

    init {
        // Observe selection language
        viewModelScope.launch {
            LanguageTranslationManager.selectedLanguageCode.collect {
                _selectedLang.value = it
            }
        }
        simulateSdkInitializations()
    }

    private fun simulateSdkInitializations() {
        viewModelScope.launch(Dispatchers.IO) {
            val sdks = listOf(
                "Loading low-latency PCM synthesizers..." to 0.15f,
                "Configuring secure offline SQLite database..." to 0.30f,
                "Initializing offline Google AdMob adapter node..." to 0.45f,
                "Starting live crash logger nodes..." to 0.60f,
                "Synchronizing analytical session trackers..." to 0.75f,
                "Loading pre-compiled synth drum packs..." to 0.90f,
                "Audio synthesizer and DSP hardware READY" to 1.0f
            )

            for (sdk in sdks) {
                _sdkInitStatus.value = sdk.first
                _sdkInitProgress.value = sdk.second
                delay(600) // Beautiful splash loading timing
            }

            // Splash Screen completed, navigate to next flow selection
            _currentScreen.value = AppScreen.LANGUAGE_SELECT
        }
    }

    fun selectLanguage(code: String) {
        LanguageTranslationManager.setLanguage(code)
    }

    fun completeLanguageSelection() {
        _currentScreen.value = AppScreen.ONBOARDING
    }

    fun nextOnboarding() {
        val current = _onboardingPage.value
        if (current < 3) {
            _onboardingPage.value = current + 1
        } else {
            _currentScreen.value = AppScreen.PAYWALL
        }
    }

    fun prevOnboarding() {
        val current = _onboardingPage.value
        if (current > 0) {
            _onboardingPage.value = current - 1
        } else {
            _currentScreen.value = AppScreen.LANGUAGE_SELECT
        }
    }

    fun skipOnboarding() {
        _currentScreen.value = AppScreen.PAYWALL
    }

    fun purchasePremiumSuccess() {
        _isPremium.value = true
        _currentScreen.value = AppScreen.WORKSPACE
    }

    fun skipPaywall() {
        // Support access to workspace for mock testing even with free version alerts if needed
        _currentScreen.value = AppScreen.WORKSPACE
    }

    fun restorePurchases() {
        // Mock restore of previous payment states
        _isPremium.value = true
        _currentScreen.value = AppScreen.WORKSPACE
    }

    fun navigateBackToWelcome() {
        _currentScreen.value = AppScreen.LANGUAGE_SELECT
        _onboardingPage.value = 0
    }

    // Hot cue interaction handlers
    fun addCuePoint(deckId: String, currentPercent: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val label = when (deckId) {
                "DECK_A" -> "Cue ${('A' + (0..5).random())}"
                else -> "Cue ${('B' + (0..5).random())}"
            }
            val color = when (deckId) {
                "DECK_A" -> "#FF007F" // Neon Pink
                else -> "#00F0FF" // Neon Cyan
            }
            repository.insertCuePoint(
                CuePoint(
                    deckId = deckId,
                    positionLabel = label,
                    positionPercent = currentPercent,
                    colorHex = color
                )
            )
        }
    }

    fun deleteCuePoint(cueId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCuePointById(cueId)
        }
    }

    // Audio recording operations
    fun toggleLiveRecording() {
        val context = getApplication<Application>()
        if (synthEngine.isRecording.value) {
            // STOP
            recordTimerJob?.cancel()
            _recordDurationText.value = "00:00"

            viewModelScope.launch {
                val result = synthEngine.stopRecording()
                if (result != null) {
                    saveRecordToDatabase(result)
                }
            }
        } else {
            // START
            val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
            val newFile = File(musicDir, "DJ_BeatMix_${System.currentTimeMillis()}.wav")
            synthEngine.startRecording(newFile)

            // Start timer counter ticking
            var seconds = 0
            recordTimerJob = viewModelScope.launch {
                while (synthEngine.isRecording.value) {
                    delay(1000)
                    seconds++
                    val mins = seconds / 60
                    val secs = seconds % 60
                    _recordDurationText.value = "%02d:%02d".format(mins, secs)
                }
            }
        }
    }

    private suspend fun saveRecordToDatabase(result: RecordResult) {
        val nextMix = RecordedMix(
            title = result.title,
            filePath = result.filePath,
            durationSeconds = result.durationSeconds,
            fileSizeBytes = result.fileSizeBytes,
            bpm = synthEngine.bpmA.value
        )
        repository.insertMix(nextMix)
    }

    fun deleteMix(mixId: Long, filePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMixById(mixId)
            try {
                val f = File(filePath)
                if (f.exists()) {
                    f.delete()
                }
            } catch (e: Exception) {
                // Ignore silent failures
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        synthEngine.release()
    }
}
