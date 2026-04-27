package com.yumaoem.feature_home.presentation.payments.plan_selection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.payments.payment_home.PaymentHomeViewModel

@Composable
fun PaymentScreenRoot(
    viewModel: PaymentHomeViewModel,
    navigateToPaymentDetailsScreen: () -> Unit
) {
    val state by viewModel.state.collectAsState()

//    FirstPaymentScreen(
//        state = state,
//        onProceedClicked = {
//            navigateToPaymentDetailsScreen()
//        },
//        onPlanSelected = { viewModel.onEvent(PaymentHomeEvent.PlanSelected(it)) },
//        onTabChanged = { planTypeId ->
//            viewModel.onEvent(PaymentHomeEvent.OnTabChanged(planTypeId))
//        }
//    )
}



//@Composable
//fun FirstPaymentScreen(
//    state: PaymentHomeState,
//    onPlanSelected: (PaymentPlanItem) -> Unit,
//    onProceedClicked: () -> Unit,
//    onTabChanged: (Int) -> Unit
//) {
//    val planTypes: List<Pair<String, Int>> by remember(state.planGroups) {
//        derivedStateOf { state.planGroups.map { it.planType to it.planTypeId } }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .padding(top = 40.dp)
//    ) {
//        Column(modifier = Modifier.align(Alignment.TopCenter)) {
//            HeaderSection()
//            HorizontalDivider(thickness = 8.dp, color = Color(0xFFF5F7FA))
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp)
//            ) {
//                Spacer(modifier = Modifier.height(16.dp))
//                Text("Buy Battery Plan", style = LocalTypography.current.bodySemiBold)
//                Spacer(modifier = Modifier.height(20.dp))
//
//                HorizontalTabSelector(
//                    options = planTypes.map { it.first },
//                    selectedTabIndex = planTypes.indexOfFirst { it.second == state.selectedPlanGroupId },
//                    onTabSelected = { index ->
//                        val selectedId = planTypes[index].second
//                        onTabChanged(selectedId)
//                    }
//                )
//
//                Spacer(modifier = Modifier.height(20.dp))
//
//                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                    items(
//                        items = state.currentPlans,
//                        key = { plan -> plan.id }
//                    ) { plan ->
//                        PaymentPlanCard(plan = plan) { onPlanSelected(plan) }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(22.dp))
//
//                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                    items(state.planDetails) { detail ->
//                        PlanDetailsItem(text = detail)
//                    }
//                }
//            }
//        }
//
//        YumaPrimaryButton(
//            enabled = state.isProceedButtonEnabled,
//            modifier = Modifier
//                .padding(horizontal = 20.dp, vertical = 24.dp)
//                .align(Alignment.BottomCenter),
//            buttonText = "Continue",
//            onClick = onProceedClicked
//        )
//    }
//}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Your Battery Plan", style = LocalTypography.current.bodySemiBold)
        Text(
            text = "No Active Plan",
            style = LocalTypography.current.smallBodyMedium.copy(
                fontSize = 12.sp,
                color = LocalColors.current.red[Colors.TYPE_500.ordinal]
            )
        )
    }
}



@Composable
fun PlanDetailsItem(
    text:String,
){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        SolidCircle3dp()
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = LocalTypography.current.smallBodyMedium.copy(
                color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun SolidCircle3dp(
    color: Color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
) {
    Box(
        modifier = Modifier
            .size(3.dp)
            .background(color = color, shape = CircleShape)
    )
}

@Preview
@Composable
fun PlanDetailsItemPreview(){
    YumaAppTheme {
        PlanDetailsItem(text = "text")
    }
}

//@Preview
//@Composable
//fun FirstPaymentScreenPreview(){
//    YumaAppTheme {
//        FirstPaymentScreen(
//            state = PaymentHomeState(
//            ),
//            onPlanSelected = {},
//            onProceedClicked = {},
//            onTabChanged = {}
//        )
//    }
//}