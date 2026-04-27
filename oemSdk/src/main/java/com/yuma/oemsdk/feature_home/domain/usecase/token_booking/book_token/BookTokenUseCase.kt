package com.yumaoem.feature_home.domain.usecase.token_booking.book_token

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.book_token.request.BookTokenRequest
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow


class BookTokenUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        bookTokenRequest: BookTokenRequest
    ): Flow<RestClientResult<BookedTokenDetails>> = repository.bookToken(bookTokenRequest)
}