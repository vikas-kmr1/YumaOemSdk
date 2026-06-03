package com.yumaoem.feature_home.presentation.payments.payment_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumacustomer.core_logger.api.LoggerApi
import com.yumacustomer.core_payments.domain.PaymentEvent
import com.yumacustomer.core_payments.domain.model.PaymentEnvironment
import com.yumacustomer.core_payments.domain.model.PaymentMode
import com.yumacustomer.core_payments.domain.model.PaymentSession
import com.yumacustomer.core_payments.domain.model.PaymentTheme
import com.yumacustomer.core_payments.domain.model.getPaymentEnvironmentForFlavour
import com.yumacustomer.core_payments.paymentManager.PaymentManager
import com.yumaoem.core.utils.orZero
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.data.dto.payment_plans.create_order.CreateOrderRequestDTO
import com.yumaoem.feature_home.data.dto.payment_plans.payment_status.PaymentStatusRequestDTO
import com.yumaoem.feature_home.domain.model.payments.UiPlan
import com.yumaoem.feature_home.domain.usecase.payments.create_order.CreateOrderUseCase
import com.yumaoem.feature_home.domain.usecase.payments.payment_status.GetPaymentStatusUseCase
import com.yumaoem.feature_home.presentation.payments.payment_details.state.PaymentDetailsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PaymentDetailsViewmodel(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getPaymentStatusUseCase: GetPaymentStatusUseCase,
    private val paymentManager: PaymentManager,
    private val preferenceApi: YumaPrefUtilApi,
    private val loggerApi: LoggerApi
) : ViewModel() {
    private val _state = MutableStateFlow(PaymentDetailsState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<PaymentDetailsUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var pollingJob: Job? = null

    fun setCurrentPlan(plan: UiPlan) {
        _state.update {
            it.copy(
                planDetails = plan
            )
        }
    }

    fun createOrder() {
        viewModelScope.launch {
            val user = preferenceApi.getUserData()
            createOrderUseCase(
                request = CreateOrderRequestDTO(
                    planId = state.value.planDetails?.id.orEmpty(),
                    clientVehicleId = user?.clientVehicleId.orZero()
                )
            ).collect(
                onLoading = {
                    _state.update { it.copy(isCreatingOrder = true) }
                },
                onSuccess = {
                    _state.update { it.copy(isCreatingOrder = false) }
                    observerPaymentStatus(sessionId = it.sessionId)
                    initiatePayment(
                        orderId = it.orderId,
                        sessionId = it.sessionId
                    )
                },
                onError = { errorMessage, errorCode ->
                    _state.update { it.copy(isCreatingOrder = false) }
                    viewModelScope.launch {
                        _uiEvent.send(PaymentDetailsUiEvent.Error(errorMessage))
                    }
                }
            )
        }
    }

    fun startPaymentStatusPolling(
        orderId: String,
        paymentId: String,
        sessionId: String
    ) {
        pollingJob?.cancel()
        _state.update { it.copy(isCreatingOrder = true) }
        pollingJob = viewModelScope.launch {
            val maxAttempts = 7
            var attempt = 0

            var shouldStop = false

            while (isActive && attempt < maxAttempts && !shouldStop) {
                getPaymentStatusUseCase(
                    request = PaymentStatusRequestDTO(
                        orderId = orderId,
                        paymentSessionId = sessionId
                    )
                ).collect(
                    onLoading = {},
                    onSuccess = { response ->

                        when (PaymentStatus.from(response.paymentStatus)) {

                            PaymentStatus.SUCCESS -> {
                                _state.update { it.copy(isCreatingOrder = false) }
                                _uiEvent.send(PaymentDetailsUiEvent.PaymentSuccess(paymentId))
                                shouldStop = true
                                pollingJob?.cancel()
                            }

                            PaymentStatus.FAILED -> {
                                _state.update { it.copy(isCreatingOrder = false) }
                                _uiEvent.send(PaymentDetailsUiEvent.PaymentFailed("Payment Failed"))
                                shouldStop = true
                                pollingJob?.cancel()
                            }

                            PaymentStatus.PENDING -> {
                                _state.update { it.copy(isCreatingOrder = true) }
                                loggerApi.logDWithTag("PaymentPolling", "Payment status: ${response.paymentStatus}")
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        loggerApi.logDWithTag("PaymentPolling", "Error: $errorMessage")
                    }
                )

                if (shouldStop) break

                attempt++
                delay(timeMillis = getDelayInMillisForAttempt(attempt))
            }

            if (attempt >= maxAttempts) {
                _uiEvent.send(PaymentDetailsUiEvent.NavigateToPaymentHomeScreen)
            }
        }
    }

    private fun getDelayInMillisForAttempt(attemptNumber: Int): Long {
        return if (attemptNumber <= 3) 1000 else 5000
    }

    fun observerPaymentStatus(
        sessionId: String
    ) {
        viewModelScope.launch {
            paymentManager.events.collect {
                when (it) {
                    is PaymentEvent.Cancelled -> {
                        viewModelScope.launch {
                            _uiEvent.send(PaymentDetailsUiEvent.PaymentFailed("Failed"))
                        }
                        loggerApi.logDWithTag("PaymentDetailsViewmodel", it.result.toString())
                    }

                    is PaymentEvent.Failure -> {
                        viewModelScope.launch {
                            _uiEvent.send(PaymentDetailsUiEvent.PaymentFailed(it.result.errorMessage))
                        }
                        loggerApi.logDWithTag("PaymentDetailsViewmodel", it.result.toString())
                    }

                    is PaymentEvent.Success -> {
                        startPaymentStatusPolling(
                            orderId = it.result.orderId,
                            paymentId = it.result.paymentId.orEmpty(),
                            sessionId = sessionId
                        )
                        loggerApi.logDWithTag("PaymentDetailsViewmodel", it.result.toString())

                    }
                }
            }
        }
    }

    fun initiatePayment(orderId: String, sessionId: String) {
        viewModelScope.launch {
            paymentManager.initiatePayment(
                session = PaymentSession(
                    orderId = orderId,
                    paymentSessionId = sessionId,
                ),
                environment = getPaymentEnvironmentForFlavour("dev"),//TOD
                mode = PaymentMode.UPI_INTENT,
                theme = PaymentTheme()
            )
        }
    }

    class Factory(
        private val createOrderUseCase: CreateOrderUseCase,
        private val getPaymentStatusUseCase: GetPaymentStatusUseCase,
        private val paymentManager: PaymentManager,
        private val preferenceApi: YumaPrefUtilApi,
        private val loggerApi: LoggerApi
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return PaymentDetailsViewmodel(
                createOrderUseCase,
                getPaymentStatusUseCase,
                paymentManager,
                preferenceApi,
                loggerApi
            ) as T
        }
    }
}

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED;

    companion object {
        fun from(value: String?): PaymentStatus =
            when {
                value.equals("Success", ignoreCase = true) -> SUCCESS
                value.equals("Failed", ignoreCase = true)  -> FAILED
                else -> PENDING
            }
    }

}



sealed class PaymentDetailsUiEvent {
    data class PaymentSuccess(val transactionId: String) : PaymentDetailsUiEvent()
    data class PaymentFailed(val message: String) : PaymentDetailsUiEvent()
    data class Error(val message: String) : PaymentDetailsUiEvent()
    data object NavigateToPaymentHomeScreen: PaymentDetailsUiEvent()
}
