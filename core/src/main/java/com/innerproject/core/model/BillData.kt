package com.innerproject.core.model

data class BillData(
    val totalAmount: Double = 0.0,
    val numberOfPeople: Int = 1,
    val tipPercentage: Double = 0.0
)