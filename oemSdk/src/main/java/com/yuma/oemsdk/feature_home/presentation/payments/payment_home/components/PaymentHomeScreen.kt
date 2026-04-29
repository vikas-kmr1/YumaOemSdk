package com.yumaoem.feature_home.presentation.payments.payment_home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yumaoem.core.utils.orFalse
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.feature_home.domain.model.payments.PaymentHomeUiHeader
import com.yumaoem.feature_home.domain.model.payments.UiCurrentPlan
import com.yumaoem.feature_home.domain.model.payments.UiPlan
import com.yumaoem.feature_home.domain.model.payments.toModifier
import com.yumaoem.feature_home.domain.model.payments.toTextStyle
import com.yumaoem.feature_home.presentation.payments.payment_home.PaymentHomeEvent
import com.yumaoem.feature_home.presentation.payments.payment_home.PaymentHomeViewModel
import com.yumaoem.feature_home.presentation.payments.payment_home.state.PaymentHomeState
import com.yumaoem.feature_home.presentation.payments.plan_selection.components.HorizontalTabSelector
import com.yumaoem.feature_home.presentation.payments.plan_selection.components.PaymentPlanCard
import com.yumaoem.feature_home.presentation.payments.plan_selection.components.PlanDetailsItem


@Composable
fun PaymentHomeScreenRoot(
    navigateToPlanDetailsScreen: (UiPlan) -> Unit,
    isPaymentsTab: Boolean,
) {
//    val viewModel = koinViewModel<PaymentHomeViewModel>()
//    val state: PaymentHomeState? by viewModel.state.collectAsState()
//
//    LaunchedEffect(isPaymentsTab) {
//        if (isPaymentsTab) {
//            viewModel.refreshPaymentData(
//                shouldShowLoader = false
//            )
//        }
//    }
//
//    if (state!= null && isPaymentsTab){
//        PaymentHomeScreen(
//            state = state!!,
//            onPlanSelected = { plan ->
//                viewModel.onEvent(PaymentHomeEvent.PlanSelected(plan))
//            },
//            onProceedClicked = {
//                viewModel.onEvent(PaymentHomeEvent.ProceedClicked)
//                if (state!!.selectedPlan!=null)
//                navigateToPlanDetailsScreen(state!!.selectedPlan!!)
//            },
//            onTabChanged = { planGroupId ->
//                viewModel.onEvent(PaymentHomeEvent.OnTabChanged(planGroupId))
//            }
//        )
//    }
//    if (state?.isLoading.orFalse()) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//    }
}


@Composable
fun PaymentHomeScreen(
    state: PaymentHomeState,
    modifier: Modifier = Modifier,
    onPlanSelected: (UiPlan) -> Unit = {},
    onProceedClicked:()-> Unit = {},
    onTabChanged:(String)-> Unit = {},
) {

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 50.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        start = LocalDimensions.current.dimen20dp,
                        end = LocalDimensions.current.dimen20dp,
                    )
            ) {
                PaymentHomeScreenHeader(
                    header = state.header
                )
                Spacer(
                    modifier = Modifier
                        .padding(top = LocalDimensions.current.dimen16dp)
                )
                HorizontalDivider(
                    thickness = LocalDimensions.current.dimen1dp,
                    color =LocalColors.current.neutral[Colors.TYPE_300.ordinal]
                )
                if (state.currentPlanDetails!=null){
                    SwapsDetailsContent(state.currentPlanDetails)
                }
            }
            HorizontalDivider(
                thickness = LocalDimensions.current.dimen8dp,
                color = LocalColors.current.neutral[Colors.TYPE_300.ordinal]
            )
            Column(
                modifier = Modifier
                    .padding(
                        start = LocalDimensions.current.dimen20dp,
                        end = LocalDimensions.current.dimen20dp,
                    )
            ) {
                Spacer(
                    modifier = Modifier
                        .padding(top = LocalDimensions.current.dimen16dp)
                )
                ExplorePlansSection(
                    state = state,
                    onTabChanged = onTabChanged,
                    onPlanSelected = onPlanSelected
                )
            }
        }

        if (state.showProceedButton){
            YumaPrimaryButton(
                enabled = state.isProceedButtonEnabled,
                modifier = Modifier
                    .padding(horizontal = LocalDimensions.current.dimen20dp, vertical = LocalDimensions.current.dimen24dp)
                    .align(Alignment.BottomCenter),
                buttonText = "Continue",
                onClick = onProceedClicked
            )
        }
    }
}

@Composable
fun SwapsDetailsContent(
    state:UiCurrentPlan
) {
    Spacer(
        modifier = Modifier
            .padding(top = LocalDimensions.current.dimen26dp)
    )
    SwapsDetailsView(
        currentPlanDetails = state
    )
    Spacer(
        modifier = Modifier
            .padding(top = LocalDimensions.current.dimen20dp)
    )
}

@Composable
fun PaymentHomeScreenHeader(
    header: PaymentHomeUiHeader,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = header.title.text,
            style = header.title.toTextStyle()
        )

        Text(
            text = header.state.text,
            style = header.state.toTextStyle(),
            modifier = header.state
                .toModifier(
                    Modifier.clip( RoundedCornerShape(16.dp))
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun SwapsDetailsView(
    currentPlanDetails: UiCurrentPlan,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            for (currentText in currentPlanDetails.swapsStatus){
                Text(
                    text = currentText.text,
                    style = currentText.toTextStyle(),
                    modifier = Modifier.alignByBaseline()
                )
            }
        }

        Text(
            text = currentPlanDetails.swapsLabel.text,
            style = currentPlanDetails.swapsLabel.toTextStyle()
        )

        Spacer(
            modifier = Modifier
                .padding(top = LocalDimensions.current.dimen18dp)
        )

        Text(
            text = currentPlanDetails.expiry.text,
            style =  currentPlanDetails.expiry.toTextStyle(),
            modifier = currentPlanDetails.expiry
                .toModifier(
                    Modifier.clip( RoundedCornerShape(16.dp))
                )
                .padding(horizontal = 30.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun ExplorePlansSection(
    state: PaymentHomeState,
    onTabChanged: (String) -> Unit = {},
    onPlanSelected: (UiPlan) -> Unit = {},
) {
    val planTypes by remember(state.planGroups) {
        derivedStateOf { state.planGroups.map { it.groupName } }
    }

    Text(
        text = state.plansSectionHeading.text,
        style = state.plansSectionHeading.toTextStyle()
    )

    Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))

    HorizontalTabSelector(
        options = planTypes,
        selectedTabIndex = state.planGroups.indexOfFirst {
            it.groupName == state.selectedPlanGroupId
        },
        onTabSelected = { index ->
            val selectedGroup = state.planGroups.getOrNull(index)
            selectedGroup?.let { onTabChanged(it.groupName) }
        }
    )

    Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))

    LazyRow(
        contentPadding = PaddingValues(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.dimen16dp)
    ) {
        items(
            items = state.currentPlans,
            key = { it.id }
        ) { plan ->
            PaymentPlanCard(
                isSelectablePlan = state.canBuyPlan,
                plan = plan,
                onClick = {
                   onPlanSelected(plan)
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(LocalDimensions.current.dimen22dp))

    Column (
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.dimen12dp)
    ) {
        for (note: String in state.footerNotes) {
            PlanDetailsItem(text = note)
        }
    }
}


//@Preview
//@Composable
//fun PaymentHomeScreenPreview(){
//    YumaAppTheme {
//        PaymentHomeScreen(
//            state = PaymentHomeState()
//        )
//    }
//}
//
//@Preview
//@Composable
//fun PaymentHomeScreenExpiredPreview(){
//    YumaAppTheme {
//        PaymentHomeScreen(
//            state = PaymentHomeState(
//                isPlanExpired = true
//            )
//        )
//    }
//}