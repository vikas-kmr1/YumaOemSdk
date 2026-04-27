package com.yumaoem.feature_home.common.paginator

import com.yumaoem.core_network.impl.data.model.RestClientResult

/**
 * A generic class for handling pagination.
 *
 * @param Key The type of the key used for pagination (e.g., page number).
 * @param Item The type of the items being paginated.
 * @property initialKey The initial key to start pagination from.
 * @property onLoadUpdated A lambda to be invoked when the loading state changes.
 * @property onRequest A suspend function that performs the actual network request to fetch a list of items.
 * @property getNextKey A suspend function that determines the next key for pagination based on the current list of items.
 * @property onError A suspend function to be invoked when an error occurs during the request.
 * @property onSuccess A suspend function to be invoked when the request is successful.
 * @author Maroof Ansari
 */
class DefaultPaginator<Key, Item>(
    private val initialKey: Key,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: Key) -> RestClientResult<List<Item>>,
    private val getNextKey: suspend (List<Item>) -> Key,
    private val onError: suspend (String) -> Unit,
    private val onSuccess: suspend (items: List<Item>, newKey: Key) -> Unit
): Paginator<Key, Item> {

    private var currentKey = initialKey
    private var isMakingRequest = false

    /**
     * Loads the next batch of items.
     * It ensures that only one request is in progress at a time.
     * It updates the loading state, performs the request, and then handles the success or error result.
     */
    override suspend fun loadNextItems() {
        if(isMakingRequest) {
            return
        }
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentKey)
        isMakingRequest = false

        when (result.status) {
            RestClientResult.Status.SUCCESS -> {
               val items = result.data ?: emptyList()
                currentKey = getNextKey(items)
                onSuccess(items, currentKey)
            }

            RestClientResult.Status.ERROR -> {
                onError(result.errorMessage.toString())
            }

            else -> {
                // Do nothing for LOADING or IDLE
            }
        }
        onLoadUpdated(false)
    }

    /**
     * Resets the paginator to its initial state, using the initial key.
     */
    override fun reset() {
        currentKey = initialKey
    }
}