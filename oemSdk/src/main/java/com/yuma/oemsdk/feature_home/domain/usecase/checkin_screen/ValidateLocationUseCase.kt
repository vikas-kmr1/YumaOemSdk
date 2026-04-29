package com.yumaoem.feature_home.domain.usecase.checkin_screen

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationRequestDTO
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationResponseDTO
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class ValidateLocationUseCase(private val repository: HomeRepository) {
    suspend operator fun invoke(request: LocationValidationRequestDTO): Flow<RestClientResult<LocationValidationResponseDTO>> {
        return repository.validateLocation(request)
    }
} 