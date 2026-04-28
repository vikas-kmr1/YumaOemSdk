package com.yumaoem.core_network.impl.util

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_onboarding.domain.model.verify_otp.response.SilentAuthResponse

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

suspend inline fun <DTO, Domain> RestClientResult<DTO>.mapFromDTO(
    crossinline dataMapper: suspend (t: DTO) -> Domain
): RestClientResult<Domain> {
    return when (status) {
        RestClientResult.Status.IDLE -> {
            RestClientResult.idle()
        }

        RestClientResult.Status.LOADING -> {
            RestClientResult.loading()
        }

        RestClientResult.Status.SUCCESS -> {
            val nonNullData = data ?: return RestClientResult.error("Data is null on success", errorCode)
            RestClientResult.success(data = dataMapper.invoke(nonNullData))
        }

        RestClientResult.Status.ERROR -> {
            RestClientResult.error(errorMessage = errorMessage.orEmpty(), errorCode = errorCode)
        }
    }
}

suspend fun <T> RestClientResult<T>.onSuccess(lambda: suspend (data: T) -> Unit): RestClientResult<T> {
    if (status == RestClientResult.Status.SUCCESS) {
        lambda.invoke(data!!)
    }
    return this
}

suspend fun <T> RestClientResult<T>.onError(lambda: suspend (errorMessage: String, errorCode: Int?) -> Unit): RestClientResult<T> {
    if (status == RestClientResult.Status.ERROR) {
        lambda.invoke(errorMessage.orEmpty(), errorCode)
    }
    return this
}

suspend fun <T> RestClientResult<T>.onLoading(lambda: suspend () -> Unit): RestClientResult<T> {
    if (status == RestClientResult.Status.LOADING) {
        lambda.invoke()
    }
    return this
}

suspend fun <T> RestClientResult<T>.isLoading() = status == RestClientResult.Status.LOADING
suspend fun <T> RestClientResult<T>.isSuccess() = status == RestClientResult.Status.SUCCESS
suspend fun <T> RestClientResult<T>.isError() = status == RestClientResult.Status.ERROR

suspend fun <T> Flow<RestClientResult<T>>.collect(
    onLoading: suspend () -> Unit,
    onSuccess: suspend (data: T) -> Unit,
    onError: suspend (errorMessage: String, errorCode: Int?) -> Unit
) {
    this.collectLatest {
        when (it.status) {
            RestClientResult.Status.IDLE -> {
                // Do nothing
            }

            RestClientResult.Status.LOADING -> {
                onLoading.invoke()
            }

            RestClientResult.Status.SUCCESS -> {
                if(it.data!=null){
                    onSuccess.invoke(it.data)
                }else{
                    onError.invoke("Data is null", null)
                }
            }

            RestClientResult.Status.ERROR -> {
                onError.invoke(it.errorMessage.orEmpty(), it.errorCode)
            }
        }
    }
}

inline fun <T> getFlowResult(
    crossinline block: suspend () -> RestClientResult<T>
): Flow<RestClientResult<T>> = flow {
    emit(RestClientResult.loading())
    try {
        emit(block())
    } catch (e: Exception) {
        emit(
            RestClientResult.error(
                errorMessage = e.message ?: "Unknown error"
            )
        )
    }
}
