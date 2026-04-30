package com.yuma.oemsdk.onboarding.utils

import com.yumaoem.core.utils.app_utils.FcmTokenHandler
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_onboarding.data.dto.fcm_token.request.InsertFcmRequest
import com.yumaoem.feature_onboarding.data.network.OnboardingRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class FcmTokenHandlerImpl(
    private val preferenceUtil: YumaPrefUtilApi,
    private val dataSource: OnboardingRemoteDataSource
):FcmTokenHandler {

    private val scope =  CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun sendFcmToken(token: String) {
        scope.launch{
            preferenceUtil.saveFcmToken(token)
            if (preferenceUtil.isUserLoggedIn()){
                insertFcmToken()
            }
        }
    }

    override suspend fun insertFcmToken() {
        scope.launch {
            val fcmToken = preferenceUtil.getFCMToken().firstOrNull()
            val userId = preferenceUtil.getUserData()?.userId
            val authToken = preferenceUtil.getBearerTokens()?.accessToken
            if (userId!= null && fcmToken!=null && authToken!=null){
                dataSource.saveFcmToken(
                    request = InsertFcmRequest(
                    userId = userId.toInt(),
                    fcmToken = fcmToken,
                    authToken = authToken
                    )
                )
            }
        }
    }
}