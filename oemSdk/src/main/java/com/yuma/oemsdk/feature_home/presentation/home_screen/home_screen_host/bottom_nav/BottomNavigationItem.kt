package com.yumaoem.feature_home.presentation.home_screen.home_screen_host.bottom_nav

import androidx.annotation.DrawableRes
import com.yuma.oemsdk.R

data class BottomNavigationItem(
    val route: NavRoute,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val isAction: Boolean = false
)

fun getMainBottomNavItems(isB2CCustomer: Boolean): List<BottomNavigationItem> {
    val items = mutableListOf(
        BottomNavigationItem(
            route = NavRoute.HELP,
            selectedIcon = R.drawable.ic_whatsapp,
            unselectedIcon = R.drawable.ic_whatsapp,
            hasNews = false,
            isAction = true
        ),
        BottomNavigationItem(
            route = NavRoute.HOME,
            selectedIcon = R.drawable.ic_yuma_selected,
            unselectedIcon = R.drawable.ic_yuma_unselected,
            hasNews = false
        )
    )

    if (isB2CCustomer) {
        items += BottomNavigationItem(
            route = NavRoute.PAYMENT,
            selectedIcon = R.drawable.ic_payment_selected,
            unselectedIcon = R.drawable.ic_payment_unselected,
            hasNews = false
        )
    }

    items += BottomNavigationItem(
        route = NavRoute.PROFILE,
        selectedIcon = R.drawable.ic_profile_selected,
        unselectedIcon = R.drawable.ic_profile_unselected,
        hasNews = false
    )

    return items
}

