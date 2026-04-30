package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuma.oemsdk.R
import com.yuma.oemsdk.YumaSdk
import com.yumaoem.core.utils.handle_permissions.HomeScreenPermissionViewModel
import com.yumaoem.core.utils.noRippleDebounceClickable
import com.yumaoem.core.utils.time_utils.formatTimeFromSeconds
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.components.buttons.YumaSecondaryButton
import com.yumaoem.core_ui.components.permission_denied_dlalog.OpenSettingsDialog
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.core_ui.utils.collectAsLaunchedEffect
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationStatus
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.ChargingStationState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.operational_station.OpenStationStateChip
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_info_bottom_sheet_common_components.GoogleMapLogoIcon
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.TokenDetailsScreenUiEvent
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.TokenDetailsViewModel
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components.diy_check_in_screen.DiyCheckInScreen
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.dialogs.CancelBookingConfirmationModalBottomSheet
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.dialogs.ReachStationModalBottomSheet
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.dialogs.TokenExpiredModalBottomSheet
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.state.DialogState
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory


@Composable
fun BookedTokenDetailsScreenRoot(
    onBookingCancelled: () -> Unit,
    onCheckedInAtStation: () -> Unit,
    onDiySwapStarted: () -> Unit,
    isHomeTab: Boolean
) {
    val viewModel: TokenDetailsViewModel = viewModel(factory = YumaSdk.tokenDetailsViewModelFactory)
    val state = viewModel.state

    viewModel.uiEvent.collectAsLaunchedEffect(Unit) { event ->
        when (event) {
            is TokenDetailsScreenUiEvent.CheckedInAtStation -> {
                onCheckedInAtStation()
            }

            is TokenDetailsScreenUiEvent.ShowSnackbar -> {
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = event.message,
                    )
                )
            }

            TokenDetailsScreenUiEvent.OnBookingCancelled -> {
                onBookingCancelled()
            }

            TokenDetailsScreenUiEvent.DiySwapStarted -> {
                onDiySwapStarted()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendTokenScreenViewed()
    }

    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    BindEffect(controller)
    val permissionViewModel = viewModel {
        HomeScreenPermissionViewModel(controller)
    }

    val bluetoothConnectState = permissionViewModel.bluetoothConnectState
    val bluetoothScanState = permissionViewModel.bluetoothScanState

    checkAndRequestBluetoothPermissions(
       bluetoothScanState = bluetoothScanState,
       permissionViewModel = permissionViewModel,
       bluetoothConnectState = bluetoothConnectState,
       controller = controller
    )

    if (isHomeTab){
//        BookedTokenDetailsScreen(
//            tokenNumber = state.bookedTokenDetails?.tokenNumber.orEmpty(),
//            tokenExpiryTime = state.expiryTime,
//            isCheckInProgress = state.isCheckInButtonLoading,
//            station = state.bookedTokenDetails?.bookingStation,
//            dialogState = state.dialogState,
//            isDiySwap = state.idDiySwap,
//            onDismissDialog = {
//                viewModel.onEvent(TokenDetailsScreenEvent.DismissDialog)
//            },
//            onBookingExpiryTryAgainClicked = {
//                viewModel.onEvent(TokenDetailsScreenEvent.BookingExpiryTryAgainClicked)
//                onBookingCancelled()
//            },
//            onCancelBookingClicked = {
//                viewModel.onEvent(TokenDetailsScreenEvent.CancelBookingClicked)
//            },
//            onCancelBookingConfirmed = {
//                viewModel.onEvent(TokenDetailsScreenEvent.CancelBookingConfirmed)
//            },
//            onCheckInAtStationClicked = {
//                viewModel.onEvent(TokenDetailsScreenEvent.CheckInAtStationClicked)
//            },
//            onGetDirectionsClicked = {
//                viewModel.onEvent(TokenDetailsScreenEvent.GetDirectionsClicked)
//            },
//            onRetryBeaconSearchClicked = {
//                viewModel.onEvent(TokenDetailsScreenEvent.NoBeaconFoundRetry)
//            }
//        )
    }
}

@Composable
private fun checkAndRequestBluetoothPermissions(
    bluetoothScanState: PermissionState,
    permissionViewModel: HomeScreenPermissionViewModel,
    bluetoothConnectState: PermissionState,
    controller: PermissionsController
) {
    val showPermissionDialog = remember { mutableStateOf(false) }

    if(showPermissionDialog.value){
        OpenSettingsDialog(
            title = "Bluetooth",
            onDismissRequest = {
                showPermissionDialog.value = false
            },
            onOpenSettingsClicked = {
                controller.openAppSettings()
            }
        )
    }


    when (bluetoothScanState) {
        PermissionState.NotDetermined, PermissionState.NotGranted -> {
            permissionViewModel.requestBluetoothScan()
        }

        PermissionState.Granted -> {
            when (bluetoothConnectState) {
                PermissionState.NotDetermined, PermissionState.NotGranted,
                PermissionState.Denied, -> {
                    permissionViewModel.requestBluetoothConnect()
                }

                PermissionState.Granted -> {}

                PermissionState.DeniedAlways -> {
                   showPermissionDialog.value = true
                }
            }
        }

        PermissionState.Denied -> {
            permissionViewModel.requestBluetoothScan()
        }

        PermissionState.DeniedAlways -> {
            showPermissionDialog.value = true
        }
    }
}



@Composable
private fun BookedTokenDetailsScreen(
    tokenNumber:String,
    tokenExpiryTime:String,
    isCheckInProgress:Boolean,
    isDiySwap: Boolean,
    station: YumaStationMarker?,
    dialogState: DialogState,
    onDismissDialog: () -> Unit,
    onRetryBeaconSearchClicked: () -> Unit,
    onBookingExpiryTryAgainClicked: () -> Unit,
    onCancelBookingConfirmed: () -> Unit,
    onCheckInAtStationClicked: () -> Unit,
    onCancelBookingClicked: () -> Unit,
    onGetDirectionsClicked: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxHeight()
    ){
        when (dialogState) {
            is DialogState.ReachStation -> ReachStationModalBottomSheet(
                title = stringResource(R.string.please_reach_the_station_to_confirm_booking),
                buttonText = stringResource(R.string.ok),
                onDismissRequest = onDismissDialog,
                onOkClicked = onDismissDialog
            )
            is DialogState.CancelBookingConfirmation -> CancelBookingConfirmationModalBottomSheet(
                onDismissRequest = onDismissDialog,
                onCancelBookingConfirmed = onCancelBookingConfirmed
            )
            is DialogState.TokenExpired -> TokenExpiredModalBottomSheet(
                onDismissRequest = onDismissDialog,
                onBookingExpiryTryAgainClicked = onBookingExpiryTryAgainClicked
            )

            DialogState.NoBeaconFound -> {
                ReachStationModalBottomSheet(
                    title = stringResource(R.string.move_close_to_the_station_and_retry),
                    buttonText = stringResource(R.string.try_again),
                    onDismissRequest = onDismissDialog,
                    onOkClicked = onRetryBeaconSearchClicked
                )
            }
            DialogState.None -> {}
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isDiySwap){
                DiyCheckInScreen(
                    modifier = Modifier
                        .padding(top = 54.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    station = station,
                    tokenNumber = tokenNumber,
                    expiryTimeLeft = tokenExpiryTime,
                    onGetDirectionsClicked = onGetDirectionsClicked
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 60.dp, bottom = 28.dp)
                            .fillMaxWidth()
                            .padding(
                                start = LocalDimensions.current.dimen20dp,
                                end = LocalDimensions.current.dimen20dp
                            ),
                        shape = LocalAppShapes.current.dialogShape,
                        colors = CardDefaults.cardColors().copy(
                            containerColor = LocalColors.current.primary[Colors.TYPE_300.ordinal]
                        )
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .padding(
                                    vertical = LocalDimensions.current.dimen24dp
                                )
                        ) {
                            TokenDetailsView(
                                tokenNumber = tokenNumber,
                                tokenExpiryTime = tokenExpiryTime,
                                modifier = Modifier
                                    .padding(horizontal = LocalDimensions.current.dimen20dp)
                            )
                            Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
                            InwardCappedDashedDivider()
                            Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
                            StationDetailsView(
                                modifier = Modifier
                                    .noRippleDebounceClickable {
                                        onGetDirectionsClicked()
                                    }
                                    .padding(horizontal = LocalDimensions.current.dimen20dp)
                                    .fillMaxWidth(),
                                stationName = station?.stationName.orEmpty(),
                                stationClosingTime = station?.stationCurrentStatus?.nextClosingTime?.formatTimeFromSeconds().toString(),
                                onDirectionsClicked = onGetDirectionsClicked
                            )
                        }
                    }
                }
            }


            BookedTokenStationDetailsFooter(
                modifier = Modifier.wrapContentHeight(),
                isDiySwap = isDiySwap,
                isCheckInProgress = isCheckInProgress,
               onGetDirectionsClicked = onGetDirectionsClicked,
               onCheckInAtStationClicked = onCheckInAtStationClicked,
               onCancelBookingClicked = onCancelBookingClicked
            )
        }
    }
}

@Composable
private fun BookedTokenStationDetailsFooter(
    isDiySwap: Boolean,
    isCheckInProgress: Boolean,
    modifier: Modifier = Modifier,
    onGetDirectionsClicked: () -> Unit,
    onCheckInAtStationClicked: () -> Unit,
    onCancelBookingClicked: () -> Unit
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen24dp))
        TokenDetailsScreenFooterView(
            isDiySwap = isDiySwap,
            isCheckInProgress = isCheckInProgress,
            modifier = Modifier.padding(all = LocalDimensions.current.dimen20dp),
            onCheckInAtStationClicked = {
                onCheckInAtStationClicked()
            },
            onCancelBookingClicked = {
                onCancelBookingClicked()
            }
        )
    }
}

@Composable
private fun TokenDetailsScreenFooterView(
    isDiySwap: Boolean,
    isCheckInProgress: Boolean,
    modifier: Modifier = Modifier,
    onCheckInAtStationClicked: () -> Unit,
    onCancelBookingClicked: () -> Unit,
){
    Column(
        modifier = modifier
    ) {
        val buttonText = if (isDiySwap) "Start swap" else stringResource(R.string.check_in_at_station)
        YumaPrimaryButton(
            isLoading = isCheckInProgress,
            buttonText = buttonText,
            onClick = {
                onCheckInAtStationClicked()
            }
        )
        Spacer(modifier = Modifier.height(LocalDimensions.current.dimen16dp))
        YumaSecondaryButton(
            buttonText = stringResource(R.string.cancel_booking),
            onClick = {
                onCancelBookingClicked()
            },
            trailingIcon = {
                TrailingIcon(
                    onClick = { onCancelBookingClicked() }
                )
            }
        )
    }
}

@Composable
fun StationDetailsView(
    modifier: Modifier = Modifier,
    stationName: String,
    stationClosingTime: String,
    onDirectionsClicked: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = LocalDimensions.current.dimen8dp)
        ) {
            Text(
                text = stationName,
                style = LocalTypography.current.bodyLargeSemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(LocalDimensions.current.dimen12dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OpenStationStateChip()

                Text(
                    text = stringResource(R.string.char_i),
                    style = LocalTypography.current.body.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_300.ordinal]
                    ),
                    modifier = Modifier.padding(horizontal = LocalDimensions.current.dimen6dp)
                )

                Text(
                    text = "Closes $stationClosingTime",
                    style = LocalTypography.current.smallBody.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
                    ),
                )
            }
        }

        GoogleMapLogoIcon(onDirectionsClicked = onDirectionsClicked)
    }
}


@Composable
fun TrailingIcon(
    trailingIcon: Painter = painterResource(R.drawable.ic_cross_red_bg),
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Image(
            modifier = Modifier
                .size(LocalDimensions.current.dimen20dp),
            painter = trailingIcon,
            contentDescription = null,
            contentScale = ContentScale.Inside,
        )
    }
}

@Preview()
@Composable
private fun PreviewBookedTokenDetailsScreen_Normal() {
    val dummyStation = YumaStationMarker(
        stationName = "Yuma Battery Swap Station - MG Road",
        stationId = 102,
        location = LatLong(12.9352, 77.6245),
        stationCurrentStatus = YumaStationStatus(
            stationState = ChargingStationState.OPEN,
            nextClosingTime = 82800
        )
    )

    YumaAppTheme {
        BookedTokenDetailsScreen(
            tokenNumber = "45",
            tokenExpiryTime = "14m 32s",
            isCheckInProgress = false,
            isDiySwap = false,
            station = dummyStation,
            dialogState = DialogState.None,
            onDismissDialog = {},
            onRetryBeaconSearchClicked = {},
            onBookingExpiryTryAgainClicked = {},
            onCancelBookingConfirmed = {},
            onCheckInAtStationClicked = {},
            onCancelBookingClicked = {},
            onGetDirectionsClicked = {}
        )
    }

}

@Preview()
@Composable
private fun PreviewBookedTokenDetailsScreen_Diy() {
    val dummyStation = YumaStationMarker(
        stationName = "Yuma DIY Swap Hub - Koramangala",
        stationId = 103,
        location = LatLong(12.9305, 77.6226),
        stationCurrentStatus = YumaStationStatus(
            stationState = ChargingStationState.DIY_ALWAYS_OPEN
        )
    )

    YumaAppTheme {
        BookedTokenDetailsScreen(
            tokenNumber = "DIY-56789",
            tokenExpiryTime = "5m 10s",
            isCheckInProgress = true,
            isDiySwap = true,
            station = dummyStation,
            dialogState = DialogState.None,
            onDismissDialog = {},
            onRetryBeaconSearchClicked = {},
            onBookingExpiryTryAgainClicked = {},
            onCancelBookingConfirmed = {},
            onCheckInAtStationClicked = {},
            onCancelBookingClicked = {},
            onGetDirectionsClicked = {}
        )
    }
}

