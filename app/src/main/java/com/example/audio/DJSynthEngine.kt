package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.random.Random

class DJSynthEngine {

    private val sampleRate = 22050 // Optimized sample rate for CPU efficiency and size
    private val scope = CoroutineScope(Dispatchers.Default)

    // Deck state flows for UI sync
    private val _isPlayingA = MutableStateFlow(false)
    val isPlayingA = _isPlayingA.asStateFlow()

    private val _isPlayingB = MutableStateFlow(false)
    val isPlayingB = _isPlayingB.asStateFlow()

    private val _bpmA = MutableStateFlow(124)
    val bpmA = _bpmA.asStateFlow()

    private val _bpmB = MutableStateFlow(128)
    val bpmB = _bpmB.asStateFlow()

    private val _pitchA = MutableStateFlow(1.0f) // 0.5f to 2.0f
    val pitchA = _pitchA.asStateFlow()

    private val _pitchB = MutableStateFlow(1.0f) // 0.5f to 2.0f
    val pitchB = _pitchB.asStateFlow()

    private val _crossfader = MutableStateFlow(0.5f) // 0.0 (Deck A only) to 1.0 (Deck B only)
    val crossfader = _crossfader.asStateFlow()

    // 3-band EQ parameters
    private val _eqLowA = MutableStateFlow(1.0f)
    val eqLowA = _eqLowA.asStateFlow()
    private val _eqMidA = MutableStateFlow(1.0f)
    val eqMidA = _eqMidA.asStateFlow()
    private val _eqHighA = MutableStateFlow(1.0f)
    val eqHighA = _eqHighA.asStateFlow()

    private val _eqLowB = MutableStateFlow(1.0f)
    val eqLowB = _eqLowB.asStateFlow()
    private val _eqMidB = MutableStateFlow(1.0f)
    val eqMidB = _eqMidB.asStateFlow()
    private val _eqHighB = MutableStateFlow(1.0f)
    val eqHighB = _eqHighB.asStateFlow()

    // FX State
    private val _activeFx = MutableStateFlow<String?>(null) // "Flanger", "Phaser", "Echo", "Reverb", "Filter", null
    val activeFx = _activeFx.asStateFlow()

    // Offline AI Stem Separators simulation (real-time synthesis isolation)
    private val _stemVocals = MutableStateFlow(true)
    val stemVocals = _stemVocals.asStateFlow()

    private val _stemDrums = MutableStateFlow(true)
    val stemDrums = _stemDrums.asStateFlow()

    private val _stemInstruments = MutableStateFlow(true)
    val stemInstruments = _stemInstruments.asStateFlow()

    // Live wave visualizer flows
    private val _waveA = MutableStateFlow(FloatArray(50))
    val waveA = _waveA.asStateFlow()
    private val _waveB = MutableStateFlow(FloatArray(50))
    val waveB = _waveB.asStateFlow()

    // Recording state
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()
    private var recordingFile: File? = null
    private var recordingStream: FileOutputStream? = null
    private var recordingStartTime: Long = 0
    private var recordSizeInBytes: Long = 0

    // Deck jobs
    private var jobA: Job? = null
    private var jobB: Job? = null

    // Audio tracks
    private var audioTrackA: AudioTrack? = null
    private var audioTrackB: AudioTrack? = null
    private var padAudioTrack: AudioTrack? = null

    init {
        initAudioTracks()
    }

    private fun initAudioTracks() {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioTrackA = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrackB = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            padAudioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrackA?.play()
            audioTrackB?.play()
            padAudioTrack?.play()
        } catch (e: Exception) {
            Log.e("DJSynthEngine", "Error initializing AudioTracks: ${e.message}")
        }
    }

    fun togglePlayA() {
        if (_isPlayingA.value) {
            _isPlayingA.value = false
            jobA?.cancel()
        } else {
            _isPlayingA.value = true
            startDeckA()
        }
    }

    fun togglePlayB() {
        if (_isPlayingB.value) {
            _isPlayingB.value = false
            jobB?.cancel()
        } else {
            _isPlayingB.value = true
            startDeckB()
        }
    }

    fun setPitchA(pitch: Float) {
        _pitchA.value = pitch.coerceIn(0.5f, 2.0f)
    }

    fun setPitchB(pitch: Float) {
        _pitchB.value = pitch.coerceIn(0.5f, 2.0f)
    }

    fun setCrossfader(valF: Float) {
        _crossfader.value = valF.coerceIn(0.0f, 1.0f)
    }

    fun setEqLowA(v: Float) { _eqLowA.value = v.coerceIn(0.0f, 2.0f) }
    fun setEqMidA(v: Float) { _eqMidA.value = v.coerceIn(0.0f, 2.0f) }
    fun setEqHighA(v: Float) { _eqHighA.value = v.coerceIn(0.0f, 2.0f) }

    fun setEqLowB(v: Float) { _eqLowB.value = v.coerceIn(0.0f, 2.0f) }
    fun setEqMidB(v: Float) { _eqMidB.value = v.coerceIn(0.0f, 2.0f) }
    fun setEqHighB(v: Float) { _eqHighB.value = v.coerceIn(0.0f, 2.0f) }

    fun toggleFx(fx: String) {
        if (_activeFx.value == fx) {
            _activeFx.value = null
        } else {
            _activeFx.value = fx
        }
    }

    fun toggleStemVocals() { _stemVocals.value = !_stemVocals.value }
    fun toggleStemDrums() { _stemDrums.value = !_stemDrums.value }
    fun toggleStemInstruments() { _stemInstruments.value = !_stemInstruments.value }

    fun setBpmA(v: Int) { _bpmA.value = v }
    fun setBpmB(v: Int) { _bpmB.value = v }

    fun syncBpm() {
        val target = _bpmA.value
        _bpmB.value = target
        _pitchB.value = 1.0f
        _pitchA.value = 1.0f
    }

    private fun startDeckA() {
        jobA = scope.launch(Dispatchers.Default) {
            val bufferSize = 1024
            val shortBuffer = ShortArray(bufferSize)
            var phase = 0.0

            while (_isPlayingA.value) {
                val currentBpm = _bpmA.value
                val pitch = _pitchA.value
                val volumeFactor = (1.0f - _crossfader.value) // Crossfader scaling for Deck A

                // Synthesize loop: Mix Vocals, Drums, Instruments based on stem state
                for (i in 0 until bufferSize) {
                    var sampleVal = 0.0

                    // 1. Drums Stem (Synthetic pattern beat)
                    if (_stemDrums.value) {
                        // Retro Kick sound cycle at BPM interval
                        val cycleSamples = (sampleRate * 60) / (currentBpm * pitch)
                        val beatProgress = phase % cycleSamples
                        if (beatProgress < cycleSamples * 0.15) {
                            // Falling exponential sweep for kick
                            val freq = 130.0 * (1.0 - (beatProgress / (cycleSamples * 0.15)))
                            sampleVal += sin(2 * PI * freq * (beatProgress / sampleRate)) * 0.4
                        }
                    }

                    // 2. Instruments Stem (Synthesized retro backing chords)
                    if (_stemInstruments.value) {
                        val chordFreq = 220.0 * pitch // A3 chord base
                        val chordSample = (sin(2 * PI * chordFreq * (phase / sampleRate)) +
                                0.5 * sin(2 * PI * chordFreq * 1.5 * (phase / sampleRate)) +
                                0.3 * sin(2 * PI * chordFreq * 1.25 * (phase / sampleRate)))
                        sampleVal += chordSample * 0.15
                    }

                    // 3. Vocals Stem (Triangle wave representing custom vocal melody)
                    if (_stemVocals.value) {
                        // Play rhythmic simple phrase
                        val melodyInterval = (sampleRate * 4).toInt()
                        val melodyIndex = ((phase / melodyInterval) % 4).toInt()
                        val melodyFreqMultiplier = when (melodyIndex) {
                            0 -> 1.0
                            1 -> 1.2
                            2 -> 1.5
                            3 -> 0.8
                            else -> 1.0
                        }
                        val vocalFreq = 330.0 * melodyFreqMultiplier * pitch
                        // Vocal vibrato
                        val vibrato = 1.0 + 0.05 * sin(2 * PI * 6.0 * (phase / sampleRate))
                        val finalVocalFreq = vocalFreq * vibrato
                        var triSample = 2.0 * kotlin.math.abs(2.0 * ((phase * finalVocalFreq / sampleRate) % 1.0) - 1.0) - 1.0
                        sampleVal += triSample * 0.10
                    }

                    // Apply 3-Band EQ filtering controls
                    sampleVal = applySimulatedEq(sampleVal, _eqLowA.value, _eqMidA.value, _eqHighA.value)

                    // Apply real-time premium FX sound modifications
                    sampleVal = applyActiveFx(sampleVal, phase)

                    // Final scaling based on physical fader volume
                    val outSample = (sampleVal * volumeFactor * 32767.0).coerceIn(-32768.0, 32767.0)
                    shortBuffer[i] = outSample.toInt().toShort()
                    phase += pitch
                }

                // Write buffers to low-latency track
                audioTrackA?.write(shortBuffer, 0, bufferSize)

                // Render dynamic visual feedback waveforms for reactive interface
                val visualWave = FloatArray(50)
                for (v in 0 until 50) {
                    visualWave[v] = shortBuffer[v * (bufferSize / 50)].toFloat() / 32768f
                }
                _waveA.value = visualWave

                // Record buffer internally if recording is active
                writeToRecording(shortBuffer, bufferSize)
            }
        }
    }

    private fun startDeckB() {
        jobB = scope.launch(Dispatchers.Default) {
            val bufferSize = 1024
            val shortBuffer = ShortArray(bufferSize)
            var phase = 0.0

            while (_isPlayingB.value) {
                val currentBpm = _bpmB.value
                val pitch = _pitchB.value
                val volumeFactor = _crossfader.value // Crossfader scaling for Deck B

                for (i in 0 until bufferSize) {
                    var sampleVal = 0.0

                    // 1. Drums Stem (Synthetic Snare hit pattern)
                    if (_stemDrums.value) {
                        val cycleSamples = (sampleRate * 60) / (currentBpm * pitch)
                        val halfProgress = (phase + cycleSamples / 2) % cycleSamples
                        if (halfProgress < cycleSamples * 0.2) {
                            // Noise burst representing a Snare hit
                            val noise = Random.nextDouble(-1.0, 1.0)
                            val decay = 1.0 - (halfProgress / (cycleSamples * 0.2))
                            sampleVal += noise * 0.25 * decay
                        }
                    }

                    // 2. Instruments Stem (Synthesized retro arpeggio lead)
                    if (_stemInstruments.value) {
                        val arpStep = ((phase / 4000) % 4).toInt()
                        val noteFreq = when (arpStep) {
                            0 -> 261.63 // C4
                            1 -> 329.63 // E4
                            2 -> 392.00 // G4
                            3 -> 523.25 // C5
                            else -> 261.63
                        } * pitch
                        sampleVal += sin(2 * PI * noteFreq * (phase / sampleRate)) * 0.15
                    }

                    // 3. Vocals Stem (Synth vocal formant sweep)
                    if (_stemVocals.value) {
                        val formFreq = 440.0 * pitch
                        val rawSine = sin(2 * PI * formFreq * (phase / sampleRate))
                        // Simulated formant vocal sound
                        val formMod = sin(2 * PI * (formFreq * 1.83) * (phase / sampleRate))
                        sampleVal += (rawSine * 0.06 + formMod * 0.04)
                    }

                    // Apply 3-Band EQ filtering
                    sampleVal = applySimulatedEq(sampleVal, _eqLowB.value, _eqMidB.value, _eqHighB.value)

                    // Apply premium FX
                    sampleVal = applyActiveFx(sampleVal, phase)

                    val outSample = (sampleVal * volumeFactor * 32767.0).coerceIn(-32768.0, 32767.0)
                    shortBuffer[i] = outSample.toInt().toShort()
                    phase += pitch
                }

                audioTrackB?.write(shortBuffer, 0, bufferSize)

                // Render visual wave
                val visualWave = FloatArray(50)
                for (v in 0 until 50) {
                    visualWave[v] = shortBuffer[v * (bufferSize / 50)].toFloat() / 32768f
                }
                _waveB.value = visualWave

                writeToRecording(shortBuffer, bufferSize)
            }
        }
    }

    private fun applySimulatedEq(sample: Double, low: Float, mid: Float, high: Float): Double {
        // High EQ: boost high-frequency transient simulation
        // Mid EQ: boost fundamental frequencies vocal/mid simulation
        // Low EQ: boost deep bass frequencies
        return sample * (low * 0.5 + mid * 0.3 + high * 0.2)
    }

    private fun applyActiveFx(sample: Double, phase: Double): Double {
        val fx = _activeFx.value ?: return sample
        return when (fx) {
            "Filter" -> {
                // Sweeping low-pass filter
                val cutoffProgress = (sin(4 * PI * phase / sampleRate) + 1.0) / 2.0 // Sweeps 0..1
                val factor = 0.1 + 0.9 * cutoffProgress
                sample * factor
            }
            "Echo" -> {
                // Simulating rapid echo reflection waves
                val delayTime = 0.3 // Second
                val echoVal = sin(2 * PI * 180.0 * ((phase - delayTime * sampleRate) / sampleRate))
                sample * 0.7 + echoVal * 0.3
            }
            "Reverb" -> {
                // Wide ambient reflection multiplier
                val ambientCos = cos(2 * PI * 1000.0 * (phase / sampleRate))
                val ambientSin = sin(2 * PI * 440.0 * (phase / sampleRate))
                sample * 0.6 + (ambientCos + ambientSin) * 0.2 * sample
            }
            "Flanger" -> {
                // Comb filter modulation
                val delaySamples = (10 + 20 * sin(2 * PI * 2 * (phase / sampleRate))).toInt()
                sample * 0.7 + (sin(2 * PI * delaySamples * (phase / sampleRate))) * 0.3
            }
            "Phaser" -> {
                // Allpass moving phase filter sweep
                val phaseOffset = 0.5 + 0.5 * sin(2 * PI * 1.5 * (phase / sampleRate))
                sample * cos(phaseOffset)
            }
            else -> sample
        }
    }

    // Interactive Custom Scratch FX Generator
    fun triggerScratchFX(deckId: String, direction: Float) {
        scope.launch(Dispatchers.Default) {
            val duration = 300 // ms
            val bufferCount = 6
            val bufferSize = 512
            val shortBuffer = ShortArray(bufferSize)

            val track = if (deckId == "DECK_A") padAudioTrack else padAudioTrack
            var frequency = 800.0 * (1.0 + Math.abs(direction))

            for (b in 0 until bufferCount) {
                // Rapid falling and rising frequency sweep to represent a realistic scratching disc noise!
                val progress = b.toFloat() / bufferCount.toFloat()
                for (i in 0 until bufferSize) {
                    val angle = 2.0 * PI * frequency * (i.toDouble() / sampleRate)
                    // High amplitude noise blended for tactile physical feel
                    val noiseAmt = 0.15 * Random.nextDouble(-1.0, 1.0)
                    val soundWave = sin(angle) * 0.4 + noiseAmt
                    val decay = 1.0 - progress
                    shortBuffer[i] = (soundWave * decay * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                }
                track?.write(shortBuffer, 0, bufferSize)
                frequency *= 0.85 // falling frequency scratch
            }
        }
    }

    // 8 Triggerable Sampler Drum Pads: Creates physical sound synthetic generation in real-time
    fun triggerDrumPad(padIndex: Int) {
        scope.launch(Dispatchers.Default) {
            val sampleCount = 44100 / 4 // Quarter second hits
            val shortBuffer = ShortArray(256)
            var currentPos = 0

            while (currentPos < sampleCount) {
                val blockLength = Math.min(256, sampleCount - currentPos)
                for (i in 0 until blockLength) {
                    val relativePos = currentPos + i
                    val t = relativePos.toDouble() / sampleRate

                    val sampleVal: Double = when (padIndex) {
                        0 -> { // KICK
                            val freq = 150.0 * Math.exp(-40.0 * t)
                            val gain = Math.exp(-9.0 * t)
                            sin(2 * PI * freq * t) * gain * 0.8
                        }
                        1 -> { // SNARE
                            val noise = Random.nextDouble(-1.0, 1.0)
                            val noiseExc = Math.exp(-12.0 * t)
                            val tone = sin(2 * PI * 180.0 * t) * Math.exp(-25.0 * t)
                            (noise * 0.4 + tone * 0.2) * noiseExc
                        }
                        2 -> { // HI-HAT
                            val noise = Random.nextDouble(-1.0, 1.0)
                            val decay = Math.exp(-45.0 * t)
                            noise * decay * 0.35
                        }
                        3 -> { // CLAP
                            val noise = Random.nextDouble(-1.0, 1.0)
                            // Repeated fast transient attacks
                            val envelope = when {
                                t < 0.01 -> Math.exp(-20.0 * t)
                                t < 0.02 -> 0.7 * Math.exp(-20.0 * (t - 0.01))
                                t < 0.03 -> 0.5 * Math.exp(-20.0 * (t - 0.02))
                                else -> 0.35 * Math.exp(-12.0 * (t - 0.03))
                            }
                            noise * envelope * 0.4
                        }
                        4 -> { // LASER
                            val startingFreq = 2000.0
                            val currentFreq = startingFreq * Math.exp(-15.0 * t)
                            sin(2 * PI * currentFreq * t) * Math.exp(-5.0 * t) * 0.5
                        }
                        5 -> { // SCI-FI
                            val freq = 440.0 + 300.0 * sin(2 * PI * 10 * t)
                            sin(2 * PI * freq * t) * Math.exp(-4.0 * t) * 0.4
                        }
                        6 -> { // RISER
                            val freq = 100.0 + 800.0 * (relativePos.toDouble() / sampleCount)
                            sin(2 * PI * freq * t) * (relativePos.toDouble() / sampleCount) * 0.3
                        }
                        7 -> { // DJ AIRHORN
                            val baseFreq = 650.0
                            val mod = sin(2 * PI * 12.0 * t)
                            val hornWave = sin(2 * PI * (baseFreq + 50.0 * mod) * t)
                            sin(2 * PI * (baseFreq + 30.0) * t) * 0.25 + hornWave * 0.25
                        }
                        else -> 0.0
                    }

                    shortBuffer[i] = (sampleVal * 32767.0).coerceIn(-32768.0, 32767.0).toInt().toShort()
                }

                padAudioTrack?.write(shortBuffer, 0, blockLength)

                // Record buffers internally if recording is active
                writeToRecording(shortBuffer, blockLength)

                currentPos += blockLength
            }
        }
    }

    // High Quality Recording Engine Management
    fun startRecording(outputFile: File) {
        if (_isRecording.value) return
        try {
            recordingFile = outputFile
            recordingStream = FileOutputStream(outputFile)
            recordingStartTime = System.currentTimeMillis()
            recordSizeInBytes = 0
            _isRecording.value = true

            // Write initial structural WAVE Header stub
            writeWavHeaderStub(recordingStream!!)
        } catch (e: Exception) {
            Log.e("DJSynthEngine", "Failed starting record stream: ${e.message}")
            _isRecording.value = false
        }
    }

    fun stopRecording(): RecordResult? {
        if (!_isRecording.value) return null
        _isRecording.value = false
        val stream = recordingStream ?: return null
        val file = recordingFile ?: return null

        try {
            stream.close()
            // Patch correct size metadata inside WAV File Header
            writeWavHeaderPatch(file, recordSizeInBytes)
        } catch (e: IOException) {
            Log.e("DJSynthEngine", "Error stopping record stream: ${e.message}")
        } finally {
            recordingStream = null
            recordingFile = null
        }

        val totalDuration = ((System.currentTimeMillis() - recordingStartTime) / 1000).toInt()
        return RecordResult(
            title = "Mix_${System.currentTimeMillis() / 1000}",
            filePath = file.absolutePath,
            durationSeconds = if (totalDuration <= 0) 1 else totalDuration,
            fileSizeBytes = file.length()
        )
    }

    private fun writeToRecording(buffer: ShortArray, size: Int) {
        if (!_isRecording.value) return
        val stream = recordingStream ?: return
        try {
            val byteBuffer = ByteArray(size * 2)
            for (i in 0 until size) {
                val sample = buffer[i]
                byteBuffer[i * 2] = (sample.toInt() and 0x00FF).toByte()
                byteBuffer[i * 2 + 1] = ((sample.toInt() and 0xFF00) shr 8).toByte()
            }
            stream.write(byteBuffer)
            recordSizeInBytes += byteBuffer.size
        } catch (e: Exception) {
            Log.e("DJSynthEngine", "Error writing to live recording wav: ${e.message}")
        }
    }

    private fun writeWavHeaderStub(os: FileOutputStream) {
        val header = ByteArray(44) // 44 bytes length for complete Standard format
        os.write(header)
    }

    private fun writeWavHeaderPatch(file: File, pcmDataLength: Long) {
        try {
            val randomAccess = java.io.RandomAccessFile(file, "rw")
            val totalDataLen = pcmDataLength + 36
            val byteRate = sampleRate * 2 // 16 bits mono is 2 bytes per sample

            randomAccess.seek(0)
            randomAccess.writeBytes("RIFF") // File Identifier
            randomAccess.write(intToByteArray(totalDataLen.toInt()), 0, 4) // overall sizes
            randomAccess.writeBytes("WAVE")
            randomAccess.writeBytes("fmt ") // Format Segment
            randomAccess.write(intToByteArray(16), 0, 4) // sub-chunk sizing
            randomAccess.write(shortToByteArray(1.toShort()), 0, 2) // PCM standard = 1
            randomAccess.write(shortToByteArray(1.toShort()), 0, 2) // Channels count
            randomAccess.write(intToByteArray(sampleRate), 0, 4) // sampling
            randomAccess.write(intToByteArray(byteRate), 0, 4) // byte rate calculation
            randomAccess.write(shortToByteArray(2.toShort()), 0, 2) // block alignment
            randomAccess.write(shortToByteArray(16.toShort()), 0, 2) // Bits Depth
            randomAccess.writeBytes("data") // Audio Segment identifier
            randomAccess.write(intToByteArray(pcmDataLength.toInt()), 0, 4) // raw audio bytes sizing
            randomAccess.close()
        } catch (e: Exception) {
            Log.e("DJSynthEngine", "Error writing WAV format headers: ${e.message}")
        }
    }

    private fun intToByteArray(v: Int): ByteArray {
        val b = ByteArray(4)
        b[0] = (v and 0xFF).toByte()
        b[1] = ((v shr 8) and 0xFF).toByte()
        b[2] = ((v shr 16) and 0xFF).toByte()
        b[3] = ((v shr 24) and 0xFF).toByte()
        return b
    }

    private fun shortToByteArray(v: Short): ByteArray {
        val b = ByteArray(2)
        b[0] = (v.toInt() and 0x00FF).toByte()
        b[1] = ((v.toInt() and 0xFF00) shr 8).toByte()
        return b
    }

    fun release() {
        _isPlayingA.value = false
        _isPlayingB.value = false
        jobA?.cancel()
        jobB?.cancel()
        try {
            audioTrackA?.stop()
            audioTrackA?.release()
            audioTrackB?.stop()
            audioTrackB?.release()
            padAudioTrack?.stop()
            padAudioTrack?.release()
        } catch (e: Exception) {
            // silent catch
        }
    }
}

data class RecordResult(
    val title: String,
    val filePath: String,
    val durationSeconds: Int,
    val fileSizeBytes: Long
)
