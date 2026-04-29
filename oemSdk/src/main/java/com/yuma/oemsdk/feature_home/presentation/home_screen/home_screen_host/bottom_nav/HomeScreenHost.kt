package com.yumaoem.feature_home.presentation.home_screen.home_screen_host.bottom_nav

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.yuma.oemsdk.YumaSdk
import com.yumaoem.core.utils.app_utils.closeApp
import com.yumaoem.core.utils.global_events.HideBottomBar
import com.yumaoem.core.utils.global_events.ShowBottomBar
import com.yumaoem.core.utils.global_events.bottom_bar_event.BottomBarEventController
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.utils.snackbar.ObserveAsEvents
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.presentation.home_screen.home_screen_host.home_navigation.HomeScreenRoot
import com.yumaoem.feature_home.presentation.payments.navigation.PaymentFlowNavigation
import com.yumaoem.feature_home.presentation.profile_screen.components.ProfileScreenRoot
import com.yumaoem.feature_home.presentation.whatsapp_support.util.openWhatsAppWithMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreenHost(
    onUserLoggedOut: () -> Unit,
    exitSdk: () -> Unit
) {
    //val yumaPrefUtilApi = koinInject<YumaPrefUtilApi>()

    var hideBottomBar by remember { mutableStateOf(false) }
    var isB2CCustomer by remember { mutableStateOf(false) }
    var bottomNavItems by remember { mutableStateOf<List<BottomNavigationItem>>(emptyList()) }

    var selectedRoute by rememberSaveable { mutableStateOf(NavRoute.HOME) }

    LaunchedEffect(Unit) {
        YumaSdk.prefManager.getB2cStatus().collect { value ->
            isB2CCustomer = value
        }
    }

    LaunchedEffect(isB2CCustomer) {
        bottomNavItems = getMainBottomNavItems(isB2CCustomer)
    }

    BackHandler(enabled = true) {
        if (selectedRoute != NavRoute.HOME) {
            selectedRoute = NavRoute.HOME
        } else {
            exitSdk()
        }
    }

    ObserveGlobalEvents { hideBottomBar = it }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Scaffold(
            bottomBar = {
                if (!hideBottomBar) {
                    BottomNavigationBar(
                        items = bottomNavItems,
                        selectedRoute = selectedRoute,
                        onItemSelected = { index, item ->
                            handleNavItemClick(item) { item ->
                                selectedRoute = item.route
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            HomeScreenContentContainer(
                selectedRoute = selectedRoute,
                onUserLoggedOut = onUserLoggedOut,
                onNavigateToPayments = {
                    selectedRoute = NavRoute.PAYMENT
                },
                onNavigateHome = {
                    selectedRoute = NavRoute.HOME
                },
                bottomPadding = paddingValues
                    .calculateBottomPadding()
            )
        }
    }
}


@Composable
private fun BottomNavigationBar(
    items: List<BottomNavigationItem>,
    selectedRoute: NavRoute,
    onItemSelected: (Int, BottomNavigationItem) -> Unit
) {
    YumaNavigationBar(containerColor = Color.White) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedRoute == item.route && !item.isAction

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index, item) },
                colors = NavigationBarItemDefaults.colors().copy(
                    selectedIndicatorColor = Color.Transparent,
                    disabledTextColor = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                    selectedTextColor = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                ),
                label = { Text(text = item.route.title) },
                alwaysShowLabel = true,
                icon = {
                    BadgedBox(
                        badge = {
                            when {
                                item.badgeCount != null -> Badge { Text(item.badgeCount.toString()) }
                                item.hasNews -> Badge()
                            }
                        }
                    ) {
                        Image(
                            modifier = Modifier.size(LocalDimensions.current.dimen24dp),
                            painter = painterResource(
                                if (isSelected) item.selectedIcon else item.unselectedIcon
                            ),
                            contentDescription = item.route.title
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeScreenContentContainer(
    selectedRoute: NavRoute,
    onUserLoggedOut: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateHome: () -> Unit,
    bottomPadding: Dp
) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .padding(bottom = bottomPadding)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (selectedRoute == NavRoute.HOME) 1f else 0f)
        ) {
            HomeScreenRoot(
                navigateToPaymentsTab = onNavigateToPayments,
                isHomeTab = (selectedRoute == NavRoute.HOME)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (selectedRoute == NavRoute.PROFILE) 1f else 0f)
        ) {
            ProfileScreenRoot(
                onLogoutClick = onUserLoggedOut,
                isProfileTab = (selectedRoute == NavRoute.PROFILE)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (selectedRoute == NavRoute.PAYMENT) 1f else 0f)
        ) {
            PaymentFlowNavigation(
                navigateToHomeTab = onNavigateHome,
                isPaymentsTab = (selectedRoute == NavRoute.PAYMENT)
            )
        }
    }
}

private fun handleNavItemClick(
    item: BottomNavigationItem,
    updateRoute: (BottomNavigationItem) -> Unit
) {
    if (item.isAction) {
        openWhatsAppWithMessage()
        val currentScreen = item.route.title
        sendCustomerSupportClickEvent(currentScreen = currentScreen)
    } else {
        updateRoute(item)
    }
}

@Composable
fun ObserveGlobalEvents(
    hideBottomBarEvent: (Boolean) -> Unit
) {
    ObserveAsEvents(
        flow = BottomBarEventController.events,
    ) { event ->
        when (event) {
            is HideBottomBar -> {
                hideBottomBarEvent(true)
            }

            is ShowBottomBar -> {
                hideBottomBarEvent(false)
            }
        }
    }
}

fun sendCustomerSupportClickEvent(
    currentScreen: String?,
) {
//    val analyticsApi: AnalyticsApi = getKoin().get()
//    val prefUtilApi: YumaPrefUtilApi = getKoin().get()
//    CoroutineScope(Dispatchers.IO).launch {
//        val currentUser = prefUtilApi.getUserData()
//        analyticsApi.postEvent(
//            event = "cs_clicked",
//            values = mapOf(
//                "user_id" to currentUser?.userId.orEmpty(),
//                "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
//                "mobile_number" to currentUser?.phone.orEmpty(),
//                "screen" to currentScreen.orEmpty()
//            )
//        )
//    }
}


