package com.innerproject.billsplitter

sealed class Screen(val route: String) {
    object BillSplitting : Screen("bill_splitting")
    object TipCalculation : Screen("tip_calculation")
    object CameraScanning : Screen("camera_scanning")
    object BillSplittingWithAmount : Screen("bill_splitting/{amount}")
    object TipCalculationWithAmount : Screen("tip_calculation/{amount}")
}