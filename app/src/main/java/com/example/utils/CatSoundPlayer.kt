package com.example.utils

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.sin

object CatSoundPlayer {
    fun playMeowSound() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val sampleRate = 44100
                val durationMs = 400
                val numSamples = (sampleRate * durationMs) / 1000
                val generatedSnd = ByteArray(numSamples * 2)

                // Procedural "Meow" sound: pitch sweep frequency modulation
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Frequency curve modeling a cute meow: rises to 800Hz then drops smoothly to 400Hz
                    val freq = if (t < 0.12) 500.0 + (t * 2500.0) else 800.0 - ((t - 0.12) * 1200.0)
                    val envelope = if (t < 0.04) t / 0.04 else if (t > 0.3) (durationMs / 1000.0 - t) / 0.1 else 1.0
                    val valSample = (sin(2.0 * Math.PI * freq * t) * 24000.0 * envelope).toInt().toShort()
                    generatedSnd[2 * i] = (valSample.toInt() and 0x00ff).toByte()
                    generatedSnd[2 * i + 1] = ((valSample.toInt() and 0xff00) ushr 8).toByte()
                }

                val audioTrack = AudioTrack.Builder()
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
                    .setBufferSizeInBytes(generatedSnd.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playPurrSound() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val sampleRate = 22050
                val durationMs = 600
                val numSamples = (sampleRate * durationMs) / 1000
                val generatedSnd = ByteArray(numSamples * 2)

                // Low purr rumble frequency ~ 35Hz with amplitude modulation
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val rumble = sin(2.0 * Math.PI * 35.0 * t) * (0.6 + 0.4 * sin(2.0 * Math.PI * 10.0 * t))
                    val envelope = if (t < 0.1) t / 0.1 else if (t > 0.5) (0.6 - t) / 0.1 else 1.0
                    val valSample = (rumble * 16383.0 * envelope).toInt().toShort()
                    generatedSnd[2 * i] = (valSample.toInt() and 0x00ff).toByte()
                    generatedSnd[2 * i + 1] = ((valSample.toInt() and 0xff00) ushr 8).toByte()
                }

                val audioTrack = AudioTrack.Builder()
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
                    .setBufferSizeInBytes(generatedSnd.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
