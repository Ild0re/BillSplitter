package com.innerproject.billsplitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.innerproject.billsplitter.ui.theme.BillSplitterTheme
import com.innerproject.features.bill_splitting.BillSplittingScreen
import com.innerproject.features.camera_scanning.CameraScanningScreen
import com.innerproject.features.tip_calculation.TipCalculationScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text(stringResource(R.string.app_name)) })
                },
                bottomBar = {
                    BottomNavigationBar(navController = navController)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.BillSplitting.route,
                        modifier = Modifier.fillMaxSize() // NavHost занимает все доступное пространство
                    ) {composable(Screen.BillSplitting.route) { BillSplittingScreen() }
                        composable(Screen.TipCalculation.route) { TipCalculationScreen() }
                        composable(Screen.CameraScanning.route) {
                            CameraScanningScreen(navController = navController) // Pass NavController
                        }
                        composable("bill_splitting/{amount}") { backStackEntry ->
                            val amount = backStackEntry.arguments?.getString("amount")?.toDoubleOrNull()
                                ?: 0.0
                            BillSplittingScreen(initialAmount = amount)
                        }
                        composable("tip_calculation/{amount}") { backStackEntry ->
                            val amount = backStackEntry.arguments?.getString("amount")?.toDoubleOrNull()
                                ?: 0.0
                            TipCalculationScreen(initialAmount = amount)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(onClick = { navController.navigate(Screen.BillSplitting.route) }) {
            Text(stringResource(R.string.bill_split))
        }
        Button(onClick = { navController.navigate(Screen.TipCalculation.route) }) {
            Text(stringResource(R.string.tip_calc))
        }
        Button(onClick = { navController.navigate(Screen.CameraScanning.route) }) {
            Text(stringResource(R.string.camera))
        }
    }
}