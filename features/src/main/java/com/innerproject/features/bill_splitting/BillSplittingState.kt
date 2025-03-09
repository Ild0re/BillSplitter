package com.innerproject.features.bill_splitting

data class BillSplittingState(
    val totalAmount: Double = 0.0,
    val numberOfPeople: Int = 1,
    val amountPerPerson: Double = 0.0
)