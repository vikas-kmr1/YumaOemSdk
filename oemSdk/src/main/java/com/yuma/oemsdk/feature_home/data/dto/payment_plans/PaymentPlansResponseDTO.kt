package com.yumaoem.feature_home.data.dto.payment_plans

import com.yumaoem.feature_home.domain.model.payments.PaymentHomeUiHeader
import com.yumaoem.feature_home.domain.model.payments.PaymentPlansUiModel
import com.yumaoem.feature_home.domain.model.payments.UiCurrentPlan
import com.yumaoem.feature_home.domain.model.payments.UiPlan
import com.yumaoem.feature_home.domain.model.payments.UiPlanGroup
import com.yumaoem.feature_home.domain.model.payments.UiText
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PaymentPlansResponseDTO(

	@SerialName("allPlans")
	val allPlans: AllPlans,

	@SerialName("footer")
	val footer: List<String>,

	@SerialName("currentPlanDetails")
	val currentPlanDetails: CurrentPlanDetails? = null,

	@SerialName("header")
	val header: Header,

	@SerialName("can_buy_plan")
	val canBuyPlan: Boolean
)

@Serializable
data class CurrentPlanDetails(

	@SerialName("expiry")
	val expiry: UiTextItem,

	@SerialName("swap_text")
	val swapText: UiTextItem,

	@SerialName("swap_count")
	val swapCount: List<UiTextItem>
)

@Serializable
data class UiTextItem(

	@SerialName("font_weight")
	val fontWeight: Int? = null,

	@SerialName("display_background_colour")
	val displayBackgroundColour: String? = null,

	@SerialName("font_size")
	val fontSize: Int? = null,

	@SerialName("type")
	val type: String? = null,

	@SerialName("display_text")
	val displayText: String,

	@SerialName("display_text_colour")
	val displayTextColour: String? = null,

	@SerialName("display_border_colour")
	val displayBorderColor: String? = null
)

@Serializable
data class Header(

	@SerialName("state")
	val state: UiTextItem,

	@SerialName("title")
	val title: UiTextItem
)

@Serializable
data class PlansGroupsItem(

	@SerialName("font_weight")
	val fontWeight: Int,

	@SerialName("plans")
	val plans: List<PlansItem>,

	@SerialName("font_size")
	val fontSize: Int,

	@SerialName("name")
	val name: String
)

@Serializable
data class AllPlans(

	@SerialName("title")
	val title: UiTextItem,

	@SerialName("plans_groups")
	val plansGroups: List<PlansGroupsItem>
)

@Serializable
data class PlansItem(

	@SerialName("tax_amount")
	val taxAmount: String,

	@SerialName("amount_without_tax")
	val amountWithoutTax: String,

	@SerialName("total_amount")
	val totalAmount: String,

	@SerialName("range")
	val range: String,

	@SerialName("id")
	val id: String,

	@SerialName("validity")
	val validity: String,

	@SerialName("title")
	val title: String
)

fun PaymentPlansResponseDTO.toUiModel(): PaymentPlansUiModel {
	return PaymentPlansUiModel(
		header = header.toUiHeader(),
		currentPlanDetails = currentPlanDetails?.toUiCurrentPlan(),
		plansSectionHeading = allPlans.title.toUiText(),
		allPlans = allPlans.plansGroups.map { it.toUiPlanGroup() },
		footerNotes = footer,
		canBuyPlan = canBuyPlan
	)
}
private fun Header.toUiHeader(): PaymentHomeUiHeader {
	return PaymentHomeUiHeader(
		title = title.toUiText(),
		state = state.toUiText()
	)
}

private fun CurrentPlanDetails.toUiCurrentPlan(): UiCurrentPlan {
	return UiCurrentPlan(
		swapsStatus = swapCount.map { it.toUiText() },
		swapsLabel = swapText.toUiText(),
		expiry = expiry.toUiText()
	)
}

private fun AllPlans.toUiPlanGroups(): List<UiPlanGroup> {
	return plansGroups.map { it.toUiPlanGroup() }
}

private fun PlansGroupsItem.toUiPlanGroup(): UiPlanGroup {
	return UiPlanGroup(
		groupName = name,
		plans = plans.map { it.toUiPlan() }
	)
}

private fun PlansItem.toUiPlan(): UiPlan {
	return UiPlan(
		id = id,
		title = title,
		range = range,
		validity = validity,
		totalAmount = totalAmount,
		amountWithoutTax = amountWithoutTax,
		taxAmount = taxAmount
	)
}

private fun UiTextItem.toUiText() = UiText(
	text = displayText,
	textColor = displayTextColour,
	backgroundColor = displayBackgroundColour,
	fontSize = fontSize,
	fontWeight = fontWeight,
	borderColor = displayBorderColor
)
