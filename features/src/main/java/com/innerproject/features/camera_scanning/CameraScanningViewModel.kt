package com.innerproject.features.camera_scanning

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class CameraScanningViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(CameraScanningState())
    val state: StateFlow<CameraScanningState> = _state

    fun onEvent(event: CameraScanningEvent) {
        when (event) {
            is CameraScanningEvent.OnImageCaptured -> {
                recognizeText(event.imageProxy)
            }

            is CameraScanningEvent.OnSplitBillClicked -> {
                _state.update { it.copy(navigateToBillSplitting = true) }
            }

            is CameraScanningEvent.OnCalculateTipClicked -> {
                _state.update { it.copy(navigateToTipCalculation = true) }
            }

            is CameraScanningEvent.AmountExtracted -> {
                _state.update { it.copy(extractedAmount = event.amount) }
            }

            is CameraScanningEvent.NavigationHandled -> {
                _state.update {
                    it.copy(
                        navigateToBillSplitting = false,
                        navigateToTipCalculation = false
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun recognizeText(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val text = visionText.text
                    _state.update { it.copy(recognizedText = text) }
                    Log.d("TextRecognition", "Recognized text: $text")
                    extractAmount(text)
                }
                .addOnFailureListener { e ->
                    Log.e("TextRecognition", "Error recognizing text", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun extractAmount(text: String) {
        val lines = text.split("n")
        var amount: Double = 0.0

        for (i in lines.indices) {
            val line = lines[i].trim().uppercase()
            if (line.contains("ИТОГО К ОПЛАТЕ") || line.contains("К ОПЛАТЕ")) {
                amount = extractAmountFromLine(line)

                if (amount == 0.0 && i + 1 < lines.size) {
                    amount = extractAmountFromLine(lines[i + 1])
                }
                break
            }
        }

        _state.update { it.copy(extractedAmount = amount) }
    }

    private fun extractAmountFromLine(line: String): Double {
        val pattern = Pattern.compile("[+-]?\\d+(?:[.,]\\d+)?")
        val matcher = pattern.matcher(line)
        var amount = 0.0

        if (matcher.find()) {
            try {
                val amountStr = matcher.group().replace(",", ".")
                amount = amountStr.toDouble()
            } catch (e: NumberFormatException) {
                Log.e("TextRecognition", "Failed to parse amount", e)
            }
        }
        return amount
    }
}
