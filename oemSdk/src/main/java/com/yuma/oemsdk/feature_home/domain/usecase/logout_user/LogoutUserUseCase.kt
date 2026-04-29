package com.yumaoem.feature_home.domain.usecase.logout_user

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.logout.LogoutResponseDTO
import com.yumaoem.feature_home.data.dto.logout.LogoutUserRequestDTO
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class LogoutUserUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        requestDTO: LogoutUserRequestDTO
    ): Flow<RestClientResult<LogoutResponseDTO>> = repository.logoutUser(requestDTO)
}