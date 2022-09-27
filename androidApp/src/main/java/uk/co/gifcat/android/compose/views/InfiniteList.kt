package uk.co.gifcat.android.compose.views

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun <T> InfiniteList(
    items: List<T>,
    modifier: Modifier = Modifier,
    onLoadMore: () -> Unit,
    rowContent: @Composable (T) -> Unit,
) {
    val listState = rememberLazyListState()
    val composableScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(items.size) { index ->
            val item = items[index]
            rowContent(item)
        }
    }

    InfiniteListHandler(listState = listState) {
        composableScope.launch {
            onLoadMore()
        }
    }
}

@Composable
fun InfiniteListHandler(
    listState: LazyListState,
    buffer: Int = 1,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            // we assume the first load is in progress if totalItemsCount is zero
            totalItemsCount > 0 && lastVisibleItemIndex > (totalItemsCount - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect { shouldLoadMore ->
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
    }
}
