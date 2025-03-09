package com.innerproject.features.tip_calculation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TipCalculationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TipCalculationState())
    val state: StateFlow<TipCalculationState> = _state

    fun onEvent(event: TipCalculationEvent) {
        when (event) {
            is TipCalculationEvent.BillAmountChanged -> {
                _state.update { it.copy(billAmount = event.amount) }
            }
            is TipCalculationEvent.TipPercentageChanged -> {
                _state.update { it.copy(tipPercentage = event.percentage.toInt()) } // Преобразуем в Int
            }
            TipCalculationEvent.Calculate -> {
                calculateTip() // Вызываем функцию расчета
            }
        }
    }

    private fun calculateTip() {
        val billAmount = _state.value.billAmount
        val tipPercentage = _state.value.tipPercentage

        val tipAmount = billAmount * (tipPercentage.toDouble() / 100)
        val totalAmount = billAmount + tipAmount

        _state.update {
            it.copy(
                tipAmount = tipAmount,
                totalAmount = totalAmount
            )
        }
    }
}