package com.yuma.oemsdk

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.yumacustomer.core_payments.domain.paymentContextProvider.PaymentContext
import com.yumacustomer.core_payments.domain.paymentContextProvider.PaymentContextProvider
import java.lang.ref.WeakReference

class AndroidPaymentContextProvider : PaymentContextProvider {

    private var currentActivityRef: WeakReference<ComponentActivity>? = null

    fun setCurrentActivity(activity: ComponentActivity) {
        currentActivityRef = WeakReference(activity)
    }

    fun clearCurrentActivity() {
        currentActivityRef = null
    }

    override suspend fun providePaymentContext(): PaymentContext {
        val activity = currentActivityRef?.get()
            ?: throw IllegalStateException("No activity available for payment. Make sure to call setCurrentActivity() first.")

        return PaymentContext(activity)
    }
}
