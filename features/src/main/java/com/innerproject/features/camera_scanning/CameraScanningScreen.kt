package com.innerproject.features.camera_scanning

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.innerproject.features.R
import java.util.concurrent.Executors

@Composable
fun CameraScanningScreen(
    viewModel: CameraScanningViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()

    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Располагаем элементы сверху и снизу
    ) {
        if (hasCamPermission) {
            CameraPreview { imageProxy ->
                viewModel.onEvent(CameraScanningEvent.OnImageCaptured(imageProxy))
            }
            Text("Recognized Text: ${state.recognizedText}")

            Row(
                modifier = Modifier
                    .fillMaxWidth() // Кнопки занимают всю ширину
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { viewModel.onEvent(CameraScanningEvent.OnSplitBillClicked) }) {
                    Text(text = "Разделение счета")
                }
                Button(onClick = { viewModel.onEvent(CameraScanningEvent.OnCalculateTipClicked) }) {
                    Text(text = "Чаевые")
                }
            }
        } else {
            Text(stringResource(R.string.camera_permission_denied))
        }
    }

    LaunchedEffect(state.navigateToBillSplitting, state.navigateToTipCalculation) {
        if (state.navigateToBillSplitting) {
            navController.navigate("bill_splitting/${state.extractedAmount}")
            viewModel.onEvent(CameraScanningEvent.NavigationHandled)  // Reset the flag
        } else if (state.navigateToTipCalculation) {
            navController.navigate("tip_calculation/${state.extractedAmount}")
            viewModel.onEvent(CameraScanningEvent.NavigationHandled)  // Reset the flag
        }
    }
}

@Composable
fun CameraPreview(onImageCaptured: (ImageProxy) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    preview.setSurfaceProvider(previewView.surfaceProvider)

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    imageAnalysis.setAnalyzer(
        Executors.newSingleThreadExecutor()
    ) { imageProxy ->
        onImageCaptured(imageProxy)
    }

    LaunchedEffect(key1 = true) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}