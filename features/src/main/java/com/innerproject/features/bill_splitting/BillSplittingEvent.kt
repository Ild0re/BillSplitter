package com.innerproject.features.bill_splitting

sealed class BillSplittingEvent {
    data class TotalAmountChanged(val amount: Double) : BillSplittingEvent()
    data class NumberOfPeopleChanged(val number: Int) : BillSplittingEvent()
    object Calculate : BillSplittingEvent()
}