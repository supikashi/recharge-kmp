package com.supikashi.recharge.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.components.CircularCarousel
import com.supikashi.recharge.components.ItemScrollMetrics
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.viewmodels.PomodoroSelectionViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.pomodoro_classic
import recharge.composeapp.generated.resources.pomodoro_deep_work
import recharge.composeapp.generated.resources.pomodoro_desc
import recharge.composeapp.generated.resources.pomodoro_extended
import recharge.composeapp.generated.resources.pomodoro_select_title
import recharge.composeapp.generated.resources.pomodoro_test
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroSelectionScreen(
    onPomodoroSelected: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: PomodoroSelectionViewModel = koinViewModel()
    val pomodoroTypes = viewModel.getPomodoroTypes()

    LaunchedEffect(viewModel) {
        viewModel.selectionCompleted.collect {
            onPomodoroSelected()
        }
    }

    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(paddingValues).padding(horizontal = 20.dp)
        ) {
            TopBar(
                leftAction = onNavigateBack,
                rightAction = onNavigateHome,
                leftIcon = Res.drawable.arrow_back,
                rightIcon = Res.drawable.home,
            )
            
            Text(
                text = stringResource(Res.string.pomodoro_select_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                textAlign = TextAlign.Center
            )
            
            var globalDescriptionHeight by remember { mutableFloatStateOf(0f) }
            
            CircularCarousel(
                items = pomodoroTypes,
                modifier = Modifier.weight(1f)
            ) { type, itemSize, metrics ->
                PomodoroTypeItem(
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
                        AnalyticsLogger.logEvent("pomodoro_type_selected", mapOf("type" to type.name))
                        viewModel.selectPomodoroType(type)
                    }
                )
            }
        }
    }
}

@Composable
private fun PomodoroTypeItem(
    type: PomodoroType,
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
                PomodoroType.DEBUG -> MaterialTheme.colorScheme.mascotPrimary
                PomodoroType.CLASSIC -> MaterialTheme.colorScheme.primary
                PomodoroType.EXTENDED -> MaterialTheme.colorScheme.tertiary
                PomodoroType.DEEP_WORK -> MaterialTheme.colorScheme.secondary
            },
            contentColor = when (type) {
                PomodoroType.DEBUG -> MaterialTheme.colorScheme.onBackground
                PomodoroType.CLASSIC -> MaterialTheme.colorScheme.background
                PomodoroType.EXTENDED -> MaterialTheme.colorScheme.onBackground
                PomodoroType.DEEP_WORK -> MaterialTheme.colorScheme.onBackground
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
                    PomodoroType.DEBUG -> stringResource(Res.string.pomodoro_test)
                    PomodoroType.CLASSIC -> stringResource(Res.string.pomodoro_classic)
                    PomodoroType.EXTENDED -> stringResource(Res.string.pomodoro_extended)
                    PomodoroType.DEEP_WORK -> stringResource(Res.string.pomodoro_deep_work)
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = stringResource(Res.string.pomodoro_desc, type.workMinutes, type.restMinutes),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .onSizeChanged { size ->
                        if (descriptionHeight == 0f) {
                            onDescriptionHeightMeasured(size.height.toFloat())
                        }
                    }
                    .padding(top = 8.dp)
                    .graphicsLayer {
                        alpha = descriptionAlpha()
                    }
            )
        }
    }
}
