package com.yuma.oemsdk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuma.oemsdk.SdkServiceLocator

/**
 * The full home experience composable, serving as the container for all
 * SDK features after successful silent authentication.
 *
 * This is the SDK equivalent of the OEM app's [HomeScreenHost].
 * It exposes a bottom navigation bar with Home, Profile, and Payments tabs.
 *
 * All dependencies are sourced from [SdkServiceLocator] — no Koin injection.
 *
 * @param onSessionExpired Called when a token refresh fails mid-session.
 *                         The [SdkNavHost] will handle navigating to [SdkAuthFailedScreen].
 */
@Composable
internal fun SdkHomeScreen(
    onSessionExpired: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(SdkTab.HOME) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            bottomBar = {
                SdkBottomNavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedTab) {
                    SdkTab.HOME -> SdkHomeTabContent(
                        locationManager = SdkServiceLocator.locationManager,
                        prefManager = SdkServiceLocator.prefManager,
                        networkClient = SdkServiceLocator.networkClient,
                        onNavigateToPayments = { selectedTab = SdkTab.PAYMENTS }
                    )
                    SdkTab.PROFILE -> SdkProfileTabContent (
                        prefManager = SdkServiceLocator.prefManager,
                        onLogout = onSessionExpired
                    )
                    SdkTab.PAYMENTS -> SdkPaymentsTabContent(
                        prefManager = SdkServiceLocator.prefManager,
                        networkClient = SdkServiceLocator.networkClient,
                        onNavigateHome = { selectedTab = SdkTab.HOME }
                    )
                }
            }
        }
    }
}

// ── Tab Definitions ───────────────────────────────────────────────────────────

internal enum class SdkTab(val label: String) {
    HOME("Home"),
    PROFILE("Profile"),
    PAYMENTS("Payments")
}

@Composable
private fun SdkBottomNavBar(
    selectedTab: SdkTab,
    onTabSelected: (SdkTab) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        SdkTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                label = { Text(tab.label, fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    selectedTextColor = Color(0xFF00C853),
                    unselectedTextColor = Color(0xFF9E9E9E)
                ),
                icon = {
                    // TODO: Replace with actual drawable resources from your design system
                    Text(
                        text = when (tab) {
                            SdkTab.HOME -> "🏠"
                            SdkTab.PROFILE -> "👤"
                            SdkTab.PAYMENTS -> "💳"
                        },
                        fontSize = 20.sp
                    )
                }
            )
        }
    }
}

// ── Tab Contents (stubs for Phase 4 wiring) ───────────────────────────────────

@Composable
private fun SdkHomeTabContent(
    locationManager: com.yuma.oemsdk.location.SdkLocationManager,
    prefManager: com.yuma.oemsdk.prefs.SdkPrefManager,
    networkClient: com.yuma.oemsdk.network.SdkNetworkClient,
    onNavigateToPayments: () -> Unit
) {
    SdkMapScreen(onNavigateToPayments = onNavigateToPayments)
}

@Composable
private fun SdkProfileTabContents(
    prefManager: com.yuma.oemsdk.prefs.SdkPrefManager,
    onLogout: () -> Unit
) {
    // Note: SdkProfileTabContent already handles its own ViewModel internally in SdkProfileScreen.kt
    // but the parameters passed here ensure consistency with the factory.
    SdkProfileTabContent(
        prefManager = prefManager,
        onLogout = onLogout
    )
}

@Composable
private fun SdkPaymentsTabContent(
    prefManager: com.yuma.oemsdk.prefs.SdkPrefManager,
    networkClient: com.yuma.oemsdk.network.SdkNetworkClient,
    onNavigateHome: () -> Unit
) {
    SdkPaymentHomeScreen(onNavigateHome = onNavigateHome)
}

@Composable
private fun PlaceholderTab(title: String) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
            Text("(Connecting feature module...)", fontSize = 14.sp, color = Color.Gray)
        }
    }
}
