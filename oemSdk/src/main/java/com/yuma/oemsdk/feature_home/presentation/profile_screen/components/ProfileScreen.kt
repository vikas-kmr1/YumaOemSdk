package com.yumaoem.feature_home.presentation.profile_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yuma.oemsdk.R
import com.yumaoem.core.utils.noRippleDebounceClickable

import com.yumaoem.core_ui.components.Yuma_elevated_card.YumaElevatedCard2

import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.feature_home.domain.model.profile.UserDetails
import com.yumaoem.feature_home.domain.model.profile.swap_history.SwapsItem
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import com.yumaoem.feature_home.presentation.profile_screen.ProfileScreenState
import com.yumaoem.feature_home.presentation.profile_screen.ProfileScreenUiEvent
import com.yumaoem.feature_home.presentation.profile_screen.ProfileViewModel
import kotlinx.coroutines.launch

import qrgenerator.qrkitpainter.rememberQrKitPainter

@Composable
fun ProfileScreenRoot(
    onLogoutClick: () -> Unit,
    isProfileTab:Boolean,
) {
    //val viewModel = koinViewModel<ProfileViewModel>()

//    val state = viewModel.state
//    val userDetails = state.userDetails
//
//    LaunchedEffect(isProfileTab) {
//        if (isProfileTab) {
//          viewModel.isProfileTab()
//        }
//    }
//
//    if (userDetails != null && isProfileTab) {
//        SwapHistoryScreen(
//            state,
//            loadNextItems = {
//                viewModel.loadNextItems()
//            },
//            onLogoutClick = {
//                viewModel.onLogout()
//            },
//            toggleBottomSheet = {
//                viewModel.toggleBottomSheet()
//            }
//        )
//    }
//
//    LaunchedEffect(Unit){
//        viewModel.sendProfileScreenLaunchedEvent()
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.uiEvent.collect { event ->
//            when (event) {
//                is ProfileScreenUiEvent.ShowError -> {
//                    SnackbarController.sendEvent(
//                        SnackbarEvent(message = event.message)
//                    )
//                }
//                ProfileScreenUiEvent.UserLoggedOut -> {
//                    onLogoutClick()
//                }
//            }
//        }
//    }
}

@Composable
fun SwapHistoryScreen(
    state: ProfileScreenState,
    loadNextItems: () -> Unit,
    onLogoutClick: () -> Unit,
    toggleBottomSheet: () -> Unit
) {
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    val userDetails = state.userDetails
    val swapData = state.swapHistory

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 65.dp, bottom = 16.dp)
    ) {

        if (showLogoutConfirmDialog) {
            LogoutConfirmationBottomSheet(
                onDismissRequest = {
                    showLogoutConfirmDialog = false
                },
                onLogoutClicked = {
                    showLogoutConfirmDialog = false
                    onLogoutClick()
                }
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .weight(1f)
        ) {
            ProfileHeader(
                name = userDetails!!.fullName,
                phone = userDetails.mobileNumber,
                profileImg = userDetails.profileImageUrl,
                state = state,
                toggleBottomSheet = toggleBottomSheet
            )
            Spacer(
                modifier = Modifier.height(16.dp)
            )
            SwapHistoryHeader()
            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (swapData.isEmpty()) {
                NoSwapHistory(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxSize()
                )
            } else {
                Box {
                    val listState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()
                    val showGoToTop by remember {
                        derivedStateOf { listState.firstVisibleItemIndex > 8 }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                    ) {
                        items(swapData.size) { index ->
                            val item = swapData[index]

                            if (index >= swapData.size - 1 && !state.endReached && !state.isRefreshing) {
                                loadNextItems()
                            }

                            when (item) {
                                is UiSwapItem.DateHeader -> {
                                    DateHeader(date = item.date)
                                }

                                is UiSwapItem.SwapItem -> {
                                    SwapHistoryItem(record = item.swap)
                                }
                            }
                        }

                        item {
                            if (state.isRefreshing) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.Black,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (showGoToTop) {
                        GotoTopButton(
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        }
                    }
                }
            }
        }
        LogoutButton(
            modifier = Modifier.wrapContentHeight(),
            onLogoutClick = {
                showLogoutConfirmDialog = !showLogoutConfirmDialog
            }
        )
    }
}

@Composable
fun NoSwapHistory(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "No swap history",
                style = LocalTypography.current.bodySemiBold.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                ),

                )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Your swap history will appear here\n once you take a battery swap.",
                style = LocalTypography.current.bodyMedium.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
                )
            )
        }

    }
}

@Composable
fun ProfileHeader(
    name: String,
    phone: String,
    profileImg: String,
    state: ProfileScreenState,
    toggleBottomSheet: () -> Unit,
) {

    if (state.isSheetOpen) {
        BikeDetailsBottomSheet(
            bikeProvider = state.userDetails?.bikeProvider ?: "UnKnown",
            bikeNumber = state.userDetails?.bikeNumber ?: "000000",
            qrNumber = state.userDetails?.bikeQrNumber ?: "AAA AAA",
            batteryIds = state.batteryDetails,
            onDismissRequest = {
                toggleBottomSheet()
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(65.dp)
        ) {
            AsyncImage(
                model = profileImg,
                contentDescription = stringResource(R.string.profile_image),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(65.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillBounds
            )
        }

        Spacer(modifier = Modifier.width(LocalDimensions.current.dimen12dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = LocalTypography.current.heading5SemiBold.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                )
            )
            Spacer(modifier = Modifier.height(LocalDimensions.current.dimen4dp))
            Text(
                text = phone,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = LocalTypography.current.body.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_600.ordinal]
                )
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        BatteryQrIcon(toggleBottomSheet)
    }
}

@Composable
fun BatteryQrIcon(toggleBottomSheet: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(LocalDimensions.current.dimen24dp))
            .background(LocalColors.current.primary[Colors.TYPE_300.ordinal])
            .padding(LocalDimensions.current.dimen8dp)
            .noRippleDebounceClickable {
                toggleBottomSheet()
            }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_scan),
            contentDescription = null,
        )
    }
}

@Composable
fun TokenQRCodeView(
    qrCodeValue: String,
    modifier: Modifier = Modifier
) {
    val yumaCenterLogo = painterResource(R.drawable.yuma_white_bg_logo)
    val painter = rememberQrKitPainter(data = qrCodeValue) {
        //logo = QrKitLogo(yumaCenterLogo)
    }
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.size(200.dp)
    )
}



@Composable
fun SwapHistoryHeader() {
    Text(
        text = stringResource(R.string.swap_history),
        style = LocalTypography.current.bodyLargeSemiBold.copy(
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
        )
    )
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                LocalColors.current.primary[Colors.TYPE_300.ordinal],
                RoundedCornerShape(LocalDimensions.current.dimen20dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.date),
            style = LocalTypography.current.bodyMedium.copy(
                color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
            )
        )
        Text(
            text = stringResource(R.string.service_time),
            style = LocalTypography.current.bodyMedium.copy(
                color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
            )
        )
    }
}

@Composable
fun DateHeader(date: String) {
    Text(
        text = date,
        style = LocalTypography.current.bodyLargeSemiBold.copy(
            color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
        ),
        modifier = Modifier.padding(
            bottom = LocalDimensions.current.dimen12dp
        )
    )
}

@Composable
fun SwapHistoryItem(record: SwapsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = LocalDimensions.current.dimen20dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = record.swapTime,
                style = LocalTypography.current.bodyMedium.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
                )
            )
        }
        Text(
            text = record.serviceTime,
            style = LocalTypography.current.body.copy(
                color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
            )
        )
    }
}

@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        HorizontalDivider(
            color = LocalColors.current.neutral[Colors.TYPE_300.ordinal],
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = LocalDimensions.current.dimen1dp
        )
        TextButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.log_out),
                style = LocalTypography.current.bodyLargeSemiBold.copy(
                    color = LocalColors.current.red[Colors.TYPE_500.ordinal]
                )
            )
            Spacer(modifier = Modifier.width(LocalDimensions.current.dimen8dp))
            Image(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = null
            )
        }
    }

}

@Composable
fun GotoTopButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        YumaElevatedCard2(
            shadowColor = Color.White,
            containerColor = YumaAppTheme.colors.neutral[Colors.TYPE_300.ordinal]
        ) {
            Row(
                modifier = Modifier
                    .noRippleDebounceClickable { onClick() }
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_goto_top_arrow),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Go to top",
                    style = LocalTypography.current.bodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                    )
                )
            }
        }
    }

}

@Preview()
@Composable
private fun PreviewSwapHistoryScreen_WithData() {
    val dummyUser = UserDetails(
        fullName = "Maroof Ansari",
        mobileNumber = "+91 98765 43210",
        profileImageUrl = "https://picsum.photos/200",
        bikeProvider = "Yuma Electric",
        bikeNumber = "KA03MN1234",
        bikeQrNumber = "QR-998877"
    )

    val dummySwaps = listOf(
        UiSwapItem.DateHeader("Nov 9, 2025"),
        UiSwapItem.SwapItem(SwapsItem("09:15 AM", "4m 23s",null)),
        UiSwapItem.SwapItem(SwapsItem("07:40 PM", "5m 08s",null)),
        UiSwapItem.DateHeader("Nov 8, 2025"),
        UiSwapItem.SwapItem(SwapsItem("01:30 PM", "3m 42s",null))
    )

    val dummyState = ProfileScreenState(
        userDetails = dummyUser,
        swapHistory = dummySwaps,
        isSheetOpen = false,
        batteryDetails = listOf(
            BatteryDetails(1234,"23412341"),
            BatteryDetails(132,"234324")
        )
    )

    YumaAppTheme {
        SwapHistoryScreen(
            state = dummyState,
            loadNextItems = {},
            onLogoutClick = {},
            toggleBottomSheet = {}
        )
    }
}

@Preview
@Composable
private fun PreviewSwapHistoryScreen_NoHistory() {
    val dummyUser = UserDetails(
        fullName = "Maroof Ansari",
        mobileNumber = "+91 90000 11111",
        profileImageUrl = "https://picsum.photos/250",
        bikeProvider = "Ather Energy",
        bikeNumber = "KA01AB9999",
        bikeQrNumber = "QR-111222"
    )

    val dummyState = ProfileScreenState(
        userDetails = dummyUser,
        swapHistory = emptyList(),
        isSheetOpen = false,
        batteryDetails = emptyList()
    )

    YumaAppTheme {
        SwapHistoryScreen(
            state = dummyState,
            loadNextItems = {},
            onLogoutClick = {},
            toggleBottomSheet = {}
        )
    }
}

