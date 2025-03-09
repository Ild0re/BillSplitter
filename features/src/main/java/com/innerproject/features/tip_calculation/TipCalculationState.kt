package com.innerproject.features.tip_calculation

data class TipCalculationState(
    val billAmount: Double = 0.0,
    val tipPercentage: Int = 15,
    val tipAmount: Double = 0.0,
    val totalAmount: Double = 0.0
)