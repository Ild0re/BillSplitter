package com.innerproject.features.camera_scanning

data class CameraScanningState(
    val recognizedText: String = "",
    val extractedAmount: Double = 0.0,
    val navigateToBillSplitting: Boolean = false,
    val navigateToTipCalculation: Boolean = false
)