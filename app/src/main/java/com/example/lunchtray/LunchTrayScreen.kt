/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum
enum class LunchTrayScreen(@StringRes val title: Int) {
    START(title = R.string.app_name),
    ENTREE(title = R.string.choose_entree),
    SIDEDISH(title = R.string.choose_side_dish),
    ACCOMPLANIMENT(title = R.string.choose_accompaniment),
    CHECKOUT(title = R.string.order_checkout)
}

// TODO: AppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupcakeAppBar(
    currentScreen: LunchTrayScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp(
    navController: NavHostController = rememberNavController(),
) {
    // TODO: Create Controller and initialization
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = LunchTrayScreen.valueOf(
        backStackEntry?.destination?.route ?: LunchTrayScreen.START.name
    )

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            // TODO: AppBar
            CupcakeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )

        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // TODO: Navigation host
        NavHost(
            navController = navController,
            startDestination = LunchTrayScreen.START.name,
        ) {
            composable(route = LunchTrayScreen.START.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navController.navigate(LunchTrayScreen.ENTREE.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            composable(route = LunchTrayScreen.ENTREE.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreen.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchTrayScreen.SIDEDISH.name)
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateEntree(item)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }

            composable(route = LunchTrayScreen.SIDEDISH.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreen.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchTrayScreen.ACCOMPLANIMENT.name)
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateSideDish(item)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }

            composable(route = LunchTrayScreen.ACCOMPLANIMENT.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreen.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchTrayScreen.CHECKOUT.name)
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateAccompaniment(item)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }

            composable(route = LunchTrayScreen.CHECKOUT.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreen.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreen.START.name, inclusive = false)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }
    }
}
