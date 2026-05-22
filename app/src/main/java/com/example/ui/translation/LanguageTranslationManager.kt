package com.example.ui.translation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SupportedLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val flagEmoji: String
)

object LanguageTranslationManager {

    val supportedLanguages = listOf(
        SupportedLanguage("en", "English", "English", "🇺🇸"),
        SupportedLanguage("es", "Spanish", "Español", "🇪🇸"),
        SupportedLanguage("fr", "French", "Français", "🇫🇷"),
        SupportedLanguage("de", "German", "Deutsch", "🇩🇪"),
        SupportedLanguage("vi", "Vietnamese", "Tiếng Việt", "🇻🇳"),
        SupportedLanguage("ja", "Japanese", "日本語", "🇯🇵"),
        SupportedLanguage("ko", "Korean", "한국어", "🇰🇷"),
        SupportedLanguage("zh", "Chinese", "简体中文", "🇨🇳"),
        SupportedLanguage("ru", "Russian", "Русский", "🇷🇺"),
        SupportedLanguage("pt", "Portuguese", "Português", "🇵🇹"),
        SupportedLanguage("it", "Italian", "Italiano", "🇮🇹"),
        SupportedLanguage("tr", "Turkish", "Türkçe", "🇹🇷"),
        SupportedLanguage("ar", "Arabic", "العربية", "🇸🇦"),
        SupportedLanguage("hi", "Hindi", "हिन्दी", "🇮🇳"),
        SupportedLanguage("pl", "Polish", "Polski", "🇵🇱"),
        SupportedLanguage("nl", "Dutch", "Nederlands", "🇳🇱"),
        SupportedLanguage("sv", "Swedish", "Svenska", "🇸🇪"),
        SupportedLanguage("no", "Norwegian", "Norsk", "🇳🇴"),
        SupportedLanguage("da", "Danish", "Dansk", "🇩🇰"),
        SupportedLanguage("fi", "Finnish", "Suomi", "🇫🇮"),
        SupportedLanguage("cs", "Czech", "Čeština", "🇨🇿"),
        SupportedLanguage("el", "Greek", "Ελληνικά", "🇬🇷"),
        SupportedLanguage("ro", "Romanian", "Română", "🇷🇴"),
        SupportedLanguage("hu", "Hungarian", "Magyar", "🇭🇺"),
        SupportedLanguage("id", "Indonesian", "Bahasa Indonesia", "🇮🇩"),
        SupportedLanguage("ms", "Malay", "Bahasa Melayu", "🇲🇾"),
        SupportedLanguage("th", "Thai", "ไทย", "🇹🇭"),
        SupportedLanguage("fil", "Tagalog", "Filipino", "🇵🇭"),
        SupportedLanguage("uk", "Ukrainian", "Українська", "🇺🇦"),
        SupportedLanguage("bg", "Bulgarian", "Български", "🇧🇬"),
        SupportedLanguage("ca", "Catalan", "Català", "🇪🇸"),
        SupportedLanguage("hr", "Croatian", "Hrvatski", "🇭🇷"),
        SupportedLanguage("he", "Hebrew", "עברית", "🇮🇱"),
        SupportedLanguage("fa", "Persian", "فارسی", "🇮🇷"),
        SupportedLanguage("ta", "Tamil", "தமிழ்", "🇮🇳"),
        SupportedLanguage("te", "Telugu", "తెలుగు", "🇮🇳"),
        SupportedLanguage("bn", "Bengali", "বাংলা", "🇧🇩"),
        SupportedLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳"),
        SupportedLanguage("gu", "Gujarati", "ગુજરાતી", "🇮🇳"),
        SupportedLanguage("sk", "Slovak", "Slovenčina", "🇸🇰"),
        SupportedLanguage("ur", "Urdu", "اردو", "🇵🇰")
    )

    private val translations = mapOf(
        "en" to mapOf(
            "title" to "BeatMix DJ",
            "subtitle" to "Offline DJ Music & Synth Workspace",
            "play_deck" to "PLAY DECK",
            "pause_deck" to "PAUSE DECK",
            "onboarding_1_title" to "Dual Virtual Turntables",
            "onboarding_1_desc" to "Scratch, pitch bend, and reverse playback high-fidelity loops with analog physical feel.",
            "onboarding_2_title" to "Pro 3-Band EQ & FX",
            "onboarding_2_desc" to "Sculpt the bass frequencies, mid mids, and crisp treble sweeps with low-latency FX filters.",
            "onboarding_3_title" to "Offline AI Stem Separator",
            "onboarding_3_desc" to "Isolate or mix Vocals, Drums, and Instruments from any track fully on-device offline.",
            "onboarding_4_title" to "Custom Sampler & Record",
            "onboarding_4_desc" to "Trigger 8 premium synth pads to lay down custom rhythms, then record and share your master mixes.",
            "selector_lang" to "Select Language",
            "paywall_title" to "Go BeatMix Premium",
            "paywall_desc" to "Unlock the ultimate club DJ toolkit. Fully offline-first, no subscription trackers required.",
            "paywall_benefit_1" to "Low-latency master DSP audio recording",
            "paywall_benefit_2" to "Pro 12 FX algorithms fully unlocked",
            "paywall_benefit_3" to "High-fidelity 24-bit PCM stem isolation",
            "paywall_benefit_4" to "Unlimited local mixes storage",
            "unlock_now" to "Unlock Premium for $4.99/mo",
            "unlock_lifetime" to "Lifetime Ownership for $19.99",
            "premium_secured" to "Secure offline backup enabled",
            "current_session" to "LIVE SESSION STATUS",
            "drum_pads" to "PRO SAMPLER PADS",
            "stems_controller" to "ON-DEVICE STEMS SEPARATOR",
            "recorded_header" to "RECORDED MIXES",
            "hot_cues" to "HOT CUES BOOKMARKS",
            "smart_sync" to "SMART SYNC TEMPO",
            "scratch_tap" to "SCRATCH TAP",
            "pitch_lbl" to "PITCH CONTROLS"
        ),
        "es" to mapOf(
            "title" to "BeatMix DJ",
            "subtitle" to "Espacio de Mezcla de DJ Sin Conexión",
            "play_deck" to "REPRODUCIR",
            "pause_deck" to "PAUSAR DECK",
            "onboarding_1_title" to "Tocadiscos Virtuales Dobles",
            "onboarding_1_desc" to "Haz scratch, pitch bend y reproducción inversa en bucles con una sensación física analógica.",
            "onboarding_2_title" to "Ecualizador de 3 Bandas y FX",
            "onboarding_2_desc" to "Esculpe los graves, medios y agudos con filtros FX de baja latencia.",
            "onboarding_3_title" to "Separador de Stems por IA",
            "onboarding_3_desc" to "Aísla o mezcla voces, batería e instrumentos en tu dispositivo sin internet.",
            "onboarding_4_title" to "Muestreador y Grabador",
            "onboarding_4_desc" to "Activa 8 pads de sintetizador premium, graba y comparte tus maravillosas grabaciones.",
            "selector_lang" to "Seleccionar Idioma",
            "paywall_title" to "Obtener BeatMix Premium",
            "paywall_desc" to "Desbloquea el kit definitivo de DJ. Completamente local y sin publicidad.",
            "paywall_benefit_1" to "Grabación de audio DSP master sin latencia",
            "paywall_benefit_2" to "Modelos de 12 efectos totalmente desbloqueados",
            "paywall_benefit_3" to "Separación de pistas en alta fidelidad de 24 bits",
            "paywall_benefit_4" to "Sesiones guardadas de forma ilimitada",
            "unlock_now" to "Desbloquear Premium por $4.99/mes",
            "unlock_lifetime" to "Propiedad de Por Vida por $19.99",
            "premium_secured" to "Transacción local cifrada segura",
            "current_session" to "ESTADO DE SESIÓN EN VIVO",
            "drum_pads" to "PADS DE MUESTREO PRO",
            "stems_controller" to "SEPARACIÓN DE PISTAS POR IA",
            "recorded_header" to "SESIONES GRABADAS",
            "hot_cues" to "CUE HOT MARCADORES",
            "smart_sync" to "TEMPO SMART SYNC",
            "scratch_tap" to "SCRATCH DIGITAL",
            "pitch_lbl" to "CONTROL DE TONO"
        ),
        "fr" to mapOf(
            "title" to "BeatMix DJ",
            "subtitle" to "Studio DJ Audio Hors-Ligne",
            "play_deck" to "JOUER PLATINE",
            "pause_deck" to "PAUSE PLATINE",
            "onboarding_1_title" to "Double Platines Virtuelles",
            "onboarding_1_desc" to "Scratch, pitch bend et lecture inversée de boucles avec un feeling analogique pur.",
            "onboarding_2_title" to "Égaliseur 3 Bandes & Effets",
            "onboarding_2_desc" to "Ajustez les basses fréquences, médiums et aigus avec des filtres FX à faible latence.",
            "onboarding_3_title" to "Séparateur de Stems IA",
            "onboarding_3_desc" to "Isolez ou mixez les voix, batteries et instruments directement sur votre téléphone.",
            "onboarding_4_title" to "Sampler & Enregistrement",
            "onboarding_4_desc" to "Lancez 8 pads de synthé pour poser des rythmes, puis enregistrez et partagez vos mixages.",
            "selector_lang" to "Choisir la Langue",
            "paywall_title" to "Passer à BeatMix Premium",
            "paywall_desc" to "Débloquez l'outil DJ ultime. 100% hors-ligne, sans traceurs commerciaux.",
            "paywall_benefit_1" to "Enregistrement audio master DSP ultra-rapide",
            "paywall_benefit_2" to "12 algorithmes d'effets Pro débloqués",
            "paywall_benefit_3" to "Séparation des Stems en PCM 24 bits",
            "paywall_benefit_4" to "Stockage local de mixages illimité",
            "unlock_now" to "Activer Premium pour 4,99 $/mois",
            "unlock_lifetime" to "Licence à vie pour 19,99 $",
            "premium_secured" to "Sauvegarde locale sécurisée active",
            "current_session" to "STATUT DE SESSION EN DIRECT",
            "drum_pads" to "POTS DE SAMPLER PRO",
            "stems_controller" to "SÉPARateur de STEMS SUR APPAREIL",
            "recorded_header" to "MIXAGES ENREGISTRÉS",
            "hot_cues" to "POINTS REPERES HOT CUES",
            "smart_sync" to "SYNCHRONISATION SMART BPM",
            "scratch_tap" to "SCRATCH TACTILE",
            "pitch_lbl" to "CONTRÔLES DU PITCH"
        )
    )

    private val _selectedLanguageCode = MutableStateFlow("en")
    val selectedLanguageCode = _selectedLanguageCode.asStateFlow()

    fun setLanguage(code: String) {
        if (supportedLanguages.any { it.code == code }) {
            _selectedLanguageCode.value = code
        }
    }

    fun getString(key: String): String {
        val currentCode = _selectedLanguageCode.value
        // Fallback directly to English translations if current translation block lacks key or translation code isn't supported
        val localizedMap = translations[currentCode] ?: translations["en"]!!
        return localizedMap[key] ?: translations["en"]!![key] ?: key
    }
}
