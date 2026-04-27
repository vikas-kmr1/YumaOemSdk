package com.yumaoem.feature_home.domain.usecase.profile_screen

import com.yumaoem.core.model.auth.User
import com.yumaoem.corepreference.api.YumaPrefUtilApi

class GetUserDetailsUseCase(
    private val yumaPrefUtilApi: YumaPrefUtilApi
){
    suspend operator fun invoke(): User? {
       return yumaPrefUtilApi.getUserData()
    }
}