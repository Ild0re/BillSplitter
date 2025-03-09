package com.innerproject.features.tip_calculation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.innerproject.features.CustomButton
import com.innerproject.features.R

@Composable
fun TipCalculationScreen(
    viewModel: TipCalculationViewModel = hiltViewModel(),
    initialAmount: Double = 0.0
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = initialAmount) {
        if (initialAmount != 0.0 && state.billAmount == 0.0) {
            viewModel.onEvent(TipCalculationEvent.BillAmountChanged(initialAmount))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.tip_calculation),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.billAmount.toString(),
            onValueChange = {
                viewModel.onEvent(
                    TipCalculationEvent.BillAmountChanged(
                        it.toDoubleOrNull() ?: 0.0
                    )
                )
            },
            label = { Text(stringResource(R.string.bill_amount)) },
            modifier = Modifier
                .fillMaxWidth()
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                        focusManager.clearFocus()
                        return@onKeyEvent true
                    }
                    false
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("${stringResource(R.string.tip_percentage)} ${state.tipPercentage}")

        Slider(
            value = state.tipPercentage.toFloat(), // Преобразуем в Float для Slider
            onValueChange = { viewModel.onEvent(TipCalculationEvent.TipPercentageChanged(it.toDouble())) },
            valueRange = 0f..30f,
            steps = 30
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("${stringResource(R.string.tip_amount)} ${String.format("%.2f", state.tipAmount)}")
        Text(
            "${stringResource(R.string.total_amount_with_tip)} ${
                String.format(
                    "%.2f",
                    state.totalAmount
                )
            }"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            text = stringResource(R.string.calculate),
            onClick = { viewModel.onEvent(TipCalculationEvent.Calculate) })
    }
}