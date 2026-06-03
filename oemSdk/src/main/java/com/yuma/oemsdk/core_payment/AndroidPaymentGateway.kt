package com.yuma.oemsdk.core_payment

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.base.exception.CFException
import com.cashfree.pg.core.api.CFSession
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutPayment
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutTheme
import com.cashfree.pg.ui.api.upi.intent.CFIntentTheme
import com.cashfree.pg.ui.api.upi.intent.CFUPIIntentCheckout
import com.cashfree.pg.ui.api.upi.intent.CFUPIIntentCheckoutPayment
import com.yumacustomer.core_payments.domain.PaymentGateway
import com.yumacustomer.core_payments.domain.model.PaymentEnvironment
import com.yumacustomer.core_payments.domain.model.PaymentMode
import com.yumacustomer.core_payments.domain.model.PaymentSession
import com.yumacustomer.core_payments.domain.model.PaymentTheme
import com.yumacustomer.core_payments.domain.paymentContextProvider.PaymentContext

class AndroidPaymentGateway(
    private val context: Context
) : PaymentGateway {

    override suspend fun initiatePayment(
        session: PaymentSession,
        environment: PaymentEnvironment,
        mode: PaymentMode,
        theme: PaymentTheme,
        context: PaymentContext,
    ): Result<Unit> {
        return try {
            val cfEnvironment = when (environment) {
                PaymentEnvironment.SANDBOX -> CFSession.Environment.SANDBOX
                PaymentEnvironment.PRODUCTION -> CFSession.Environment.PRODUCTION
            }

            val cfSession = CFSession.CFSessionBuilder()
                .setEnvironment(cfEnvironment)
                .setPaymentSessionID(session.paymentSessionId)
                .setOrderId(session.orderId)
                .build()

            val activity = context.activity

            when (mode) {
                PaymentMode.WEB_CHECKOUT -> {
                    initiateWebCheckout(activity, cfSession, theme)
                }
                PaymentMode.UPI_INTENT -> {
                    initiateUPIIntent(activity, cfSession, theme)
                }
            }

            Result.success(Unit)

        } catch (e: CFException) {
            Result.failure(Exception("Cashfree error: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(Exception("Payment initiation failed: ${e.message}", e))
        }
    }

    override fun isUPIAppAvailable(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun getAvailableUPIApps(): List<String> {
        val upiPackages = listOf(
            "com.phonepe.app",
            "com.google.android.apps.nfc.payment",
            "net.one97.paytm",
            "in.org.npci.upiapp",
            "in.amazon.mShop.android.shopping"
        )

        return upiPackages.filter { isUPIAppAvailable(it) }
    }

    private fun initiateWebCheckout(
        activity: ComponentActivity,
        cfSession: CFSession,
        theme: PaymentTheme?
    ) {

        val webCheckout= CFWebCheckoutPayment
            .CFWebCheckoutPaymentBuilder()
            .setSession(cfSession)
            .build()

        CFPaymentGatewayService.getInstance().doPayment(activity, webCheckout)
    }

    private fun initiateUPIIntent(
        activity: ComponentActivity,
        cfSession: CFSession,
        theme: PaymentTheme?,
    ) {
        val intentTheme = theme?.let { createIntentTheme(it) }

        val upiCheckoutBuilder = CFUPIIntentCheckout.CFUPIIntentBuilder()

        val upiCheckout = upiCheckoutBuilder.build()

        val payment = CFUPIIntentCheckoutPayment.CFUPIIntentPaymentBuilder()
            .setSession(cfSession)
            .setCfUPIIntentCheckout(upiCheckout)
            //.apply { intentTheme?.let { setCfIntentTheme(it) } }
            .build()

        CFPaymentGatewayService.getInstance().doPayment(activity, payment)
    }

    private fun createWebTheme(theme: PaymentTheme): CFWebCheckoutTheme {
        return CFWebCheckoutTheme.CFWebCheckoutThemeBuilder()
            .setNavigationBarTextColor(theme.primaryColor)
            .build()
    }

    private fun createIntentTheme(theme: PaymentTheme): CFIntentTheme {
        return CFIntentTheme.CFIntentThemeBuilder()
            .setBackgroundColor(theme.primaryColor)
            .setPrimaryTextColor(theme.primaryTextColor)
            .build()
    }
}