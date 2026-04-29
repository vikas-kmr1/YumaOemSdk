package com.yumaoem.feature_home.common.paginator

/**
 * An interface for implementing pagination.
 *
 * @param Key The type of the key used for pagination (e.g., page number).
 * @param Item The type of the items being paginated.
 * @author Maroof Ansari
 */
interface Paginator<Key, Item> {
    /**
     * Loads the next batch of items.
     */
    suspend fun loadNextItems()

    /**
     * Resets the paginator to its initial state.
     */
    fun reset()
}
