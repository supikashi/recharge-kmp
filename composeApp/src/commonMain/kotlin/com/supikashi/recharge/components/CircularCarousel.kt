package com.supikashi.recharge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
data class ItemScrollMetrics(
    val translationY: Float = 0f,
    val scale: Float = 1f,
    val zIndex: Float = 0f,
    val alpha: Float = 0f
)

@Composable
fun <T> CircularCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemFraction: Float = 0.99f,
    itemContent: @Composable (item: T, size: Dp, metrics: ItemScrollMetrics) -> Unit
) {
    if (items.isEmpty()) return

    val initialIndex = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % items.size)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val density = LocalDensity.current.density
    
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val padding = 5.dp
        val availableWidth = maxWidth - padding * 2
        val availableHeight = maxHeight - padding * 2
        
        val aspectRatio = 0.535f
        // Ensure sizes are valid correctly bounds within constraints
        val targetWidth = androidx.compose.ui.unit.min(availableWidth, availableHeight * aspectRatio)
        val targetHeight = targetWidth / aspectRatio

        val itemSize = targetWidth * itemFraction
        val halfHeight = targetHeight / 2 - itemSize / 2

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = halfHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.size(width = targetWidth, height = targetHeight)
        ) {
            items(Int.MAX_VALUE) { index ->
                val actualIndex = index % items.size
                val item = items[actualIndex]

                val metrics by remember(index) {
                    derivedStateOf {
                        val currentItemInfo = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                        if (currentItemInfo != null) {
                            val viewportCenter = (listState.layoutInfo.viewportStartOffset + listState.layoutInfo.viewportEndOffset) / 2f
                            val itemCenter = currentItemInfo.offset + (currentItemInfo.size / 2f)

                            val y = itemCenter - viewportCenter
                            val radius = (itemSize.value * density * 2f / PI).toFloat()

                            val angle = (y / radius)
                            val visualY = (radius * sin(angle))
                            val tY = visualY - y

                            val distance = (1f - cos(angle)) / 2
                            val scale = 1f - distance.toFloat()

                            val normalizedY = (y.absoluteValue / radius).coerceIn(0f, 1f)
                            val descAlpha = (1f - normalizedY).coerceIn(0f, 1f)

                            ItemScrollMetrics(
                                translationY = tY,
                                scale = scale,
                                zIndex = scale,
                                alpha = descAlpha
                            )
                        } else {
                            ItemScrollMetrics()
                        }
                    }
                }

                itemContent(item, itemSize, metrics)
            }
        }
    }
}
