package com.innerproject.features.bill_splitting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BillSplittingViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(BillSplittingState())
    val state: StateFlow<BillSplittingState> = _state

    fun onEvent(event: BillSplittingEvent) {
        when (event) {
            is BillSplittingEvent.TotalAmountChanged -> {
                _state.update { it.copy(totalAmount = event.amount) }
            }
            is BillSplittingEvent.NumberOfPeopleChanged -> {
                _state.update { it.copy(numberOfPeople = event.number) }
            }
            BillSplittingEvent.Calculate -> {
                calculateAmountPerPerson() // Вызываем функцию расчета
            }
        }
    }

    private fun calculateAmountPerPerson() {
        val totalAmount = _state.value.totalAmount
        val numberOfPeople = _state.value.numberOfPeople

        if (totalAmount > 0 && numberOfPeople > 0) {
            val amountPerPerson = totalAmount / numberOfPeople
            _state.update { it.copy(amountPerPerson = amountPerPerson) }
        } else {
            _state.update { it.copy(amountPerPerson = 0.0) }
        }
    }
}