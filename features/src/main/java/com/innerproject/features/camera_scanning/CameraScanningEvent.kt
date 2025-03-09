package com.innerproject.features.camera_scanning

import androidx.camera.core.ImageProxy

sealed class CameraScanningEvent {
    data class OnImageCaptured(val imageProxy: ImageProxy) : CameraScanningEvent()
    object OnSplitBillClicked : CameraScanningEvent()
    object OnCalculateTipClicked : CameraScanningEvent()
    data class AmountExtracted(val amount: Double) : CameraScanningEvent()
    object NavigationHandled : CameraScanningEvent()
}