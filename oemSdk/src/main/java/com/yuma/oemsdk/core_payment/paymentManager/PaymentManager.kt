package com.yumacustomer.core_payments.paymentManager


import com.yumacustomer.core_payments.domain.PaymentEvent
import com.yumacustomer.core_payments.domain.PaymentGateway
import com.yumacustomer.core_payments.domain.model.PaymentEnvironment
import com.yumacustomer.core_payments.domain.model.PaymentMode
import com.yumacustomer.core_payments.domain.model.PaymentResult
import com.yumacustomer.core_payments.domain.model.PaymentSession
import com.yumacustomer.core_payments.domain.model.PaymentTheme
import com.yumacustomer.core_payments.domain.paymentContextProvider.PaymentContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Main entry point for payment processing.
 * Handles payment flow and provides callbacks to consumer modules.
 */
class PaymentManager(
    private val paymentGateway: PaymentGateway,
    private val contextProvider: PaymentContextProvider,
    //private val loggerApi: LoggerApi,
) {

    private val TAG = "PaymentManager"

    val scope = CoroutineScope(Dispatchers.Main+ SupervisorJob())

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private val _events = MutableSharedFlow<PaymentEvent>()
    val events = _events.asSharedFlow()

    fun notifySuccess(orderId: String) {
        val result = PaymentResult.Success(orderId)
        scope.launch {
            _events.emit(PaymentEvent.Success(result))
        }
    }

    fun notifyFailure(
        orderId: String,
        code: String,
        message: String
    ) {
        val result = PaymentResult.Failure(
            orderId = orderId,
            errorCode = code,
            errorMessage = message,
            errorDescription = ""
        )
        scope.launch {
            _events.emit(PaymentEvent.Failure(result))
        }
    }

    fun notifyCancelled(orderId: String) {
        val result = PaymentResult.Cancelled(orderId)
        scope.launch {
            _events.tryEmit(PaymentEvent.Cancelled(result))
        }
    }

    init {
                //loggerApi.logDWithTag(TAG, "Initializing PaymentManager")
    }

    /**
     * Initiates payment with the provided session details
     */
    suspend fun initiatePayment(
        session: PaymentSession,
        environment: PaymentEnvironment = PaymentEnvironment.PRODUCTION,
        mode: PaymentMode = PaymentMode.UPI_INTENT,
        theme: PaymentTheme = PaymentTheme(),
    ): Result<Unit> {

                //loggerApi.logDWithTag( TAG, "initiatePayment() called with sessionId=${session.orderId}, environment=$environment, mode=$mode" )

        _paymentState.value = PaymentState.Loading
                //loggerApi.logDWithTag(TAG, "Payment state updated: Loading")

        return try {
            val context = contextProvider.providePaymentContext()
            //loggerApi.logDWithTag(TAG, "Context provided for payment initiation")

            val result = paymentGateway.initiatePayment(
                context = context,
                session = session,
                environment = environment,
                mode = mode,
                theme = theme,
            )

            result.onSuccess {
                //loggerApi.logDWithTag(TAG, "Payment initiation successful for orderId=${session.orderId}")
            }.onFailure { error ->
                //loggerApi.logEWithTag( TAG, "Payment initiation failed for orderId=${session.orderId}: ${error.message}", Exception(error) )

                _paymentState.value = PaymentState.Error(error.message ?: "Unknown error")
            }

            result
        } catch (e: Exception) {
            //loggerApi.logEWithTag(TAG, "Exception during payment initiation", e)
            _paymentState.value = PaymentState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * Checks if a UPI app is available on the device
     */
    fun isUPIAppAvailable(packageName: String): Boolean {
                //loggerApi.logDWithTag(TAG, "Checking UPI availability for package: $packageName")
        val available = paymentGateway.isUPIAppAvailable(packageName)
                //loggerApi.logDWithTag(TAG, "UPI availability result: $available")
        return available
    }

    /**
     * Gets list of available UPI apps on the device
     */
    fun getAvailableUPIApps(): List<String> {
                //loggerApi.logDWithTag(TAG, "Fetching available UPI apps")
        val apps = paymentGateway.getAvailableUPIApps()
                //loggerApi.logDWithTag(TAG, "Available UPI apps: $apps")
        return apps
    }

    /**
     * Resets the payment state to idle
     */
    fun resetState() {
                //loggerApi.logDWithTag(TAG, "Resetting payment state")
        _paymentState.value = PaymentState.Idle
                //loggerApi.logDWithTag(TAG, "Payment state reset to Idle")
    }
}


sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val result: PaymentResult.Success) : PaymentState()
    data class Failed(val result: PaymentResult.Failure) : PaymentState()
    data class Cancelled(val result: PaymentResult.Cancelled) : PaymentState()
    data class Error(val message: String) : PaymentState()
}