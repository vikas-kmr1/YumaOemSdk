package com.yumaoem.feature_home.presentation.payments.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.yumaoem.core_ui.utils.animation.defaultEnterTransition
import com.yumaoem.core_ui.utils.animation.defaultExitTransition
import com.yumaoem.core_ui.utils.animation.defaultPopEnterTransition
import com.yumaoem.core_ui.utils.animation.defaultPopExitTransition
import com.yumaoem.feature_home.domain.model.payments.UiPlan

import com.yumaoem.feature_home.presentation.payments.payment_details.PaymentDetailsScreenRoot
import com.yumaoem.feature_home.presentation.payments.payment_home.components.PaymentHomeScreenRoot
import com.yumaoem.feature_home.presentation.payments.payment_success.PaymentSuccessScreenRoot
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PaymentFlowNavigation(
    navigateToHomeTab: () -> Unit,
    isPaymentsTab:Boolean
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination.toString()

    val scope = rememberCoroutineScope()

    LaunchedEffect(currentRoute, isPaymentsTab) {
        if (!isPaymentsTab) return@LaunchedEffect

        when {
            currentRoute.contains("PaymentDetailsScreen") -> {
               // hideBottomNavigationBar()
            }
            else -> {
                //showBottomNavigationBar()
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = PaymentHomeScreen,
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        popExitTransition = defaultPopExitTransition
    ) {

        composable<PaymentDetailsScreen> {  backStackEntry ->
            val planJson = backStackEntry.toRoute<PaymentDetailsScreen>().planJson
            val plan: UiPlan = Json.decodeFromString<UiPlan>(planJson)

            PaymentDetailsScreenRoot(
                isPaymentsTab = isPaymentsTab,
                plan = plan,
                onBackClicked = { navController.popBackStack() },
                navigateToPaymentSuccessScreen = {
                    navController.navigate(PaymentSuccessScreen)
                },
                navigateToHomeTab = {
                    navController.navigate(PaymentHomeScreen) {
                        popUpTo(PaymentHomeScreen) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<PaymentSuccessScreen> {
            PaymentSuccessScreenRoot(
                navigateToHomeTab = {
                    navController.navigate(PaymentHomeScreen) {
                        popUpTo(PaymentHomeScreen) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                    scope.launch {
                        navigateToHomeTab()
                    }
                }
            )
        }


        composable<PaymentHomeScreen> {
            PaymentHomeScreenRoot(
                isPaymentsTab = isPaymentsTab,
                navigateToPlanDetailsScreen = { selectedPlan: UiPlan ->
                    val planJson = Json.encodeToString(selectedPlan)
                    navController.navigate(PaymentDetailsScreen(planJson))
                }
            )
        }
    }
}
