package com.justappz.aniyomitv.extensions_management.presentation.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.justappz.aniyomitv.extensions_management.domain.model.ExtensionDomain

class ExtensionPagingSource(
    private val extensions: List<ExtensionDomain>,
) : PagingSource<Int, ExtensionDomain>() {

    override fun getRefreshKey(state: PagingState<Int, ExtensionDomain>): Int? {
        // Return the key of the page that should be loaded on refresh
        return state.anchorPosition?.let { anchorPos ->
            val anchorPage = state.closestPageToPosition(anchorPos)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ExtensionDomain> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        val fromIndex = page * pageSize
        val toIndex = minOf(fromIndex + pageSize, extensions.size)

        if (fromIndex >= extensions.size) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }

        val pageItems = extensions.subList(fromIndex, toIndex)
        val nextKey = if (toIndex < extensions.size) page + 1 else null

        return LoadResult.Page(
            data = pageItems,
            prevKey = if (page == 0) null else page - 1,
            nextKey = nextKey,
        )
    }
}
