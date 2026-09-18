package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class CatSoundPlayer {

    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var purrJob: Job? = null

    /** Tiếng "meo" — quét tần số từ 650Hz → 950Hz → 500Hz trong ~380ms. */
    fun playMeow() {
        scope.launch {
            val durationMs = 380
            val n = sampleRate * durationMs / 1000
            val samples = ShortArray(n)
            var phase = 0.0
            for (i in 0 until n) {
                val t = i.toDouble() / n
                // Sweep: 650 → 950 → 500 Hz
                val f = when {
                    t < 0.35 -> 650.0 + (950.0 - 650.0) * (t / 0.35)
                    t < 0.7  -> 950.0 - (950.0 - 800.0) * ((t - 0.35) / 0.35)
                    else     -> 800.0 - (800.0 - 500.0) * ((t - 0.7) / 0.3)
                }
                phase += 2 * PI * f / sampleRate
                val vibrato = 1.0 + 0.04 * sin(2 * PI * 25.0 * t)  // rung 25Hz
                val env = envelope(t)
                val sample = sin(phase) * vibrato * env * 0.55
                samples[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(-32768, 32767).toShort()
            }
            playPcm(samples)
        }
    }

    /** Tiếng "grừ grừ" — rumble 70Hz + AM 24Hz, lặp cho tới khi stopPurr(). */
    fun startPurr() {
        if (purrJob?.isActive == true) return
        purrJob = scope.launch {
            val durationMs = 1200
            val n = sampleRate * durationMs / 1000
            val samples = ShortArray(n)
            for (i in 0 until n) {
                val t = i.toDouble() / sampleRate
                val base = sin(2 * PI * 70.0 * t) * 0.6
                val am = 0.5 + 0.5 * sin(2 * PI * 24.0 * t)   // biên độ nhấp nhô
                val noise = (Random.nextDouble() - 0.5) * 0.08
                val s = (base * am + noise) * 0.35
                samples[i] = (s * Short.MAX_VALUE).toInt().coerceIn(-32768, 32767).toShort()
            }
            while (isActive) playPcmBlocking(samples)
        }
    }

    fun stopPurr() {
        purrJob?.cancel()
        purrJob = null
    }

    fun release() {
        scope.cancel()
    }

    // -------- helpers --------

    private fun envelope(t: Double): Double {
        val attack = 0.05
        val release = 0.25
        return when {
            t < attack -> t / attack
            t > 1 - release -> (1 - t) / release
            else -> 1.0
        }
    }

    private fun playPcm(samples: ShortArray) {
        val track = buildTrack(samples.size)
        track.write(samples, 0, samples.size)
        track.play()
        // Tự release sau khi phát xong
        scope.launch {
            delay(samples.size * 1000L / sampleRate + 80)
            runCatching { track.stop(); track.release() }
        }
    }

    private suspend fun playPcmBlocking(samples: ShortArray) {
        val track = buildTrack(samples.size)
        track.write(samples, 0, samples.size)
        track.play()
        delay(samples.size * 1000L / sampleRate)
        runCatching { track.stop(); track.release() }
    }

    private fun buildTrack(sizeInShorts: Int): AudioTrack {
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        return AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
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
            .setBufferSizeInBytes(maxOf(minBuf, sizeInShorts * 2))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }
}
