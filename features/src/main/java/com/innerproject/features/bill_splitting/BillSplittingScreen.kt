package com.innerproject.features.bill_splitting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.innerproject.features.CustomButton
import com.innerproject.features.R
import com.innerproject.features.ui.theme.BillSplitterTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillSplittingScreen(
    viewModel: BillSplittingViewModel = hiltViewModel(),
    initialAmount: Double = 0.0
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = initialAmount) {
        if (initialAmount != 0.0 && state.totalAmount == 0.0) {
            viewModel.onEvent(BillSplittingEvent.TotalAmountChanged(initialAmount))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.bill_splitting),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.totalAmount.toString(),
            onValueChange = {
                val newValue = it.toDoubleOrNull() ?: 0.0
                viewModel.onEvent(BillSplittingEvent.TotalAmountChanged(newValue))
            },
            label = { Text(stringResource(R.string.total_amount)) },
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

        OutlinedTextField(
            value = state.numberOfPeople.toString(),
            onValueChange = {
                val newValue = it.toIntOrNull() ?: 1
                viewModel.onEvent(BillSplittingEvent.NumberOfPeopleChanged(newValue))
            },
            label = { Text(stringResource(R.string.number_of_people)) },
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "${stringResource(R.string.amount_per_person)} ${
                String.format(
                    "%.2f",
                    state.amountPerPerson
                )
            }"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            text = stringResource(R.string.calculate),
            onClick = { viewModel.onEvent(BillSplittingEvent.Calculate) })
    }
}


@Preview(showBackground = true)
@Composable
fun BillSplittingScreenPreview() {
    BillSplitterTheme {
        BillSplittingScreen()
    }
}