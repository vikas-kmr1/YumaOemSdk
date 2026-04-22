package com.yuma.oemsdk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuma.oemsdk.SdkServiceLocator
import com.yuma.oemsdk.data.dto.SdkPlanDto
import com.yuma.oemsdk.viewmodel.SdkPaymentHomeViewModel

@Composable
internal fun SdkPaymentHomeScreen(
    onNavigateHome: () -> Unit
) {
    val factory = SdkPaymentHomeViewModel.Factory(
        prefManager = SdkServiceLocator.prefManager,
        remoteDataSource = SdkServiceLocator.remoteDataSource
    )
    val viewModel: SdkPaymentHomeViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Available Plans",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00C853))
            }
        } else if (state.plans.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No plans available", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.plans) { plan ->
                    SdkPlanItem(
                        plan = plan,
                        isSelected = state.selectedPlan?.planId == plan.planId,
                        onSelect = { viewModel.selectPlan(plan) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Proceed to buy */ },
                enabled = state.selectedPlan != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
            ) {
                Text("Proceed to Pay", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SdkPlanItem(
    plan: SdkPlanDto,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00C853)) else null,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)),
        onClick = onSelect
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = plan.planName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "₹${plan.price}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF00C853))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = plan.description, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Validity: ${plan.validityDays ?: 0} days", fontSize = 12.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Swaps: ${plan.swapLimit ?: "Unlimited"}", fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}
