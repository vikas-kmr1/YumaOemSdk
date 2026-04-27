package com.yumaoem.core_network.impl.data.base

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.core_network.impl.model.YumaErrorResponseDTO
import com.yumaoem.core_network.impl.util.NetworkConstants.NetworkErrorMessages
import com.yumaoem.core_network.impl.util.NetworkConstants.NetworkErrorCodes
import com.yumaoem.core_network.impl.util.NetworkApiEvent
import com.yumaoem.core_network.impl.util.NetworkConstants.NetworkErrorMessages.ACCESS_TOKEN_EXPIRED
import com.yumaoem.core_network.impl.util.NetworkEventBus
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.JsonConvertException
import io.ktor.util.network.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException

/**
 * Base class for data sources that provides common functionality for handling network requests.
 * It includes a generic method to execute network calls and handle responses and errors,
 * as well as a retry mechanism for failed operations.
 */
abstract class BaseDataSource {

    /**
     * Executes a network call and converts the response into a [RestClientResult].
     * This function handles various network and serialization exceptions and maps them to specific error codes and messages.
     *
     * @param T The type of the data expected in a successful response.
     * @param call A suspend lambda function that returns an [HttpResponse]. This is the actual network call to be executed.
     * @return A [RestClientResult] which is either [RestClientResult.Success] with the deserialized data
     *         or [RestClientResult.Error] with an error message and code.
     */
    protected suspend inline fun <reified T> getResult(call: () -> HttpResponse): RestClientResult<T> {
        val result: HttpResponse?
        return try {
            result = call()
            if (result.status == HttpStatusCode.OK || result.status == HttpStatusCode.Created || result.status == HttpStatusCode.Accepted) {
                val apiResponse: ApiResponse<T> = result.body()
                RestClientResult.success(apiResponse.data)
            } else {
                RestClientResult.error(
                    errorMessage = NetworkErrorMessages.SOME_ERROR_OCCURRED,
                    errorCode = result.status.value
                )
            }
        } catch (e: ClientRequestException) {
            val statusCode = e.response.status.value
            val errorBody = try {
                e.response.body<YumaErrorResponseDTO>().message
            } catch (parseException: Exception) {
                null
            }

            return when (statusCode) {
                NetworkErrorCodes.ACCESS_TOKEN_EXPIRED -> {
                    RestClientResult.error(
                        errorMessage = errorBody?: ACCESS_TOKEN_EXPIRED,
                        errorCode = statusCode
                    )
                }

                NetworkErrorCodes.REFRESH_TOKEN_EXPIRED -> {
                    NetworkEventBus.INSTANCE.invokeEvent(NetworkApiEvent.REFRESH_TOKEN_EXPIRED)
                    RestClientResult.error(
                        errorMessage = NetworkErrorMessages.PLEASE_LOGIN_AGAIN,
                        errorCode = statusCode
                    )
                }

                else -> {
                    RestClientResult.error(
                        errorMessage = errorBody ?: e.message ?: NetworkErrorMessages.SOME_ERROR_OCCURRED,
                        errorCode = statusCode
                    )
                }
            }
        }
        catch (e: ServerResponseException) {
            val statusCode = e.response.status.value
            RestClientResult.error(
                errorMessage = NetworkErrorMessages.APP_UNDER_MAINTENANCE,
                errorCode = statusCode
            )
        } catch (e: IOException) {
            RestClientResult.error(
                errorMessage = NetworkErrorMessages.PLEASE_CHECK_YOUR_INTERNET_CONNECTION,
                errorCode = NetworkErrorCodes.INTERNET_NOT_WORKING
            )
        } catch (e: UnresolvedAddressException) {
            RestClientResult.error(
                errorMessage = NetworkErrorMessages.PLEASE_CHECK_YOUR_INTERNET_CONNECTION,
                errorCode = NetworkErrorCodes.INTERNET_NOT_WORKING
            )
        } catch (e: SocketTimeoutException) {
            RestClientResult.error(
                errorMessage = NetworkErrorMessages.PLEASE_CHECK_YOUR_INTERNET_CONNECTION,
                errorCode = NetworkErrorCodes.INTERNET_NOT_WORKING
            )
        } catch (e: SerializationException) {
            RestClientResult.error(
                errorMessage = NetworkErrorMessages.DATA_SERIALIZATION_ERROR,
                errorCode = NetworkErrorCodes.DATA_SERIALIZATION_ERROR,
            )
        } catch (e: JsonConvertException) {
            RestClientResult.error(
                errorMessage = NetworkErrorMessages.DATA_SERIALIZATION_ERROR,
                errorCode = NetworkErrorCodes.DATA_SERIALIZATION_ERROR
            )
        } catch (e: CancellationException) {
            RestClientResult.error(
                errorMessage = "",  //This is a special case in which we don't want to show any error
                errorCode = NetworkErrorCodes.NETWORK_CALL_CANCELLED
            )
        } catch (e: Exception) {
            RestClientResult.error(
                errorMessage = e.message ?: NetworkErrorMessages.SOME_ERROR_OCCURRED,
                errorCode = NetworkErrorCodes.UNKNOWN_ERROR_OCCURRED
            )
        }
    }

    /**
     * Retries a block of code with exponential backoff.
     * This is useful for operations that might fail transiently, such as network requests.
     *
     * @param T The return type of the block to be executed.
     * @param times The maximum number of times to try the operation. Defaults to [Int.MAX_VALUE].
     * @param initialDelay The initial delay in milliseconds before the first retry. Defaults to 100ms.
     * @param maxDelay The maximum delay in milliseconds between retries. Defaults to 1000ms.
     * @param factor The factor by which the delay should be multiplied for each subsequent retry. Defaults to 2.0.
     * @param block The suspend lambda function to be executed and retried.
     * @param shouldRetry A lambda that takes the result of the `block` and returns `true` if the operation should be retried.
     * @return The result of the `block` execution.
     */
    protected suspend fun <T> retryIOs(
        times: Int = Int.MAX_VALUE,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T,
        shouldRetry: (result: T) -> Boolean
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) {
            val result = block()
            if (shouldRetry(result).not()) {
                return result
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return block()
    }
}
