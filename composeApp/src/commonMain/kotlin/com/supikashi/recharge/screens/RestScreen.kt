package com.supikashi.recharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.components.CircularCarousel
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.models.RestType
import com.supikashi.recharge.theme.mascotPrimary
import org.jetbrains.compose.resources.stringResource
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.rest_desc_active
import recharge.composeapp.generated.resources.rest_desc_calm
import recharge.composeapp.generated.resources.rest_desc_creative
import recharge.composeapp.generated.resources.rest_select_title
import recharge.composeapp.generated.resources.rest_type_active
import recharge.composeapp.generated.resources.rest_type_calm
import recharge.composeapp.generated.resources.rest_type_creative


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestScreen(
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToType: (RestType) -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues).padding(horizontal = 20.dp)
        ) {
            TopBar(
                leftAction = onNavigateBack,
                rightAction = onNavigateHome,
                leftIcon = Res.drawable.arrow_back,
                rightIcon = Res.drawable.home,
            )
            
            Text(
                text = stringResource(Res.string.rest_select_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            var globalDescriptionHeight by remember { mutableFloatStateOf(0f) }
            
            CircularCarousel(
                items = RestType.entries,
                modifier = Modifier.weight(1f)
            ) { type, itemSize, metrics ->
                RestTypeItem(
                    type = type,
                    descriptionHeight = globalDescriptionHeight,
                    onDescriptionHeightMeasured = { globalDescriptionHeight = it },
                    modifier = Modifier
                        .size(itemSize)
                        .graphicsLayer {
                            translationY = metrics.translationY
                            scaleX = metrics.scale
                            scaleY = metrics.scale
                        }
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(placeable.width, placeable.height) {
                                placeable.place(0, 0, zIndex = metrics.zIndex)
                            }
                        },
                    descriptionAlpha = { metrics.alpha },
                    onSelect = {
                        AnalyticsLogger.logEvent("rest_type_selected", mapOf("type" to type.name))
                        onNavigateToType(type)
                    }
                )
            }
        }
    }
}

@Composable
private fun RestTypeItem(
    type: RestType,
    modifier: Modifier = Modifier,
    descriptionAlpha: () -> Float,
    descriptionHeight: Float,
    onDescriptionHeightMeasured: (Float) -> Unit,
    onSelect: () -> Unit
) {
    Button(
        onClick = onSelect,
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = when (type) {
                RestType.ACTIVE -> MaterialTheme.colorScheme.primary
                RestType.CALM -> MaterialTheme.colorScheme.tertiary
                RestType.CREATIVE -> MaterialTheme.colorScheme.secondary
            },
            contentColor = when (type) {
                RestType.ACTIVE -> MaterialTheme.colorScheme.background
                RestType.CALM -> MaterialTheme.colorScheme.onBackground
                RestType.CREATIVE -> MaterialTheme.colorScheme.onBackground
            }
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.graphicsLayer {
                val alphaVal = descriptionAlpha()
                translationY = (1f - alphaVal) * (descriptionHeight / 2f + 4.dp.toPx())
            }
        ) {
            Text(
                text = when (type) {
                    RestType.CALM -> stringResource(Res.string.rest_type_calm)
                    RestType.ACTIVE -> stringResource(Res.string.rest_type_active)
                    RestType.CREATIVE -> stringResource(Res.string.rest_type_creative)
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = when (type) {
                    RestType.CALM -> stringResource(Res.string.rest_desc_calm)
                    RestType.ACTIVE -> stringResource(Res.string.rest_desc_active)
                    RestType.CREATIVE -> stringResource(Res.string.rest_desc_creative)
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .onSizeChanged { size ->
                        if (descriptionHeight == 0f) {
                            onDescriptionHeightMeasured(size.height.toFloat())
                        }
                    }
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        val a = descriptionAlpha()
                        alpha = a * a
                    }
            )
        }
    }
}
