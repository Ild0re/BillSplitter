package com.innerproject.features.tip_calculation

sealed class TipCalculationEvent {
    data class BillAmountChanged(val amount: Double) : TipCalculationEvent()
    data class TipPercentageChanged(val percentage: Double) : TipCalculationEvent()
    object Calculate : TipCalculationEvent()
}