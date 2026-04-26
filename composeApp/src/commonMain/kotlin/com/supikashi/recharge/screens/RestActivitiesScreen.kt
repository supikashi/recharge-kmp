package com.supikashi.recharge.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.data.getActivitiesForType
import com.supikashi.recharge.models.RestActivity
import com.supikashi.recharge.models.RestType
import com.supikashi.recharge.theme.mascotPrimary
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.supikashi.recharge.analytics.AnalyticsLogger
import kotlin.math.absoluteValue
import com.supikashi.recharge.models.CardContent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RestActivitiesScreen(
    type: RestType,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val localDensity = LocalDensity.current
    var sourceHeight by remember { mutableStateOf(0.dp) }
    val activities = getActivitiesForType(type)

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.mascotPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(sourceHeight))

                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    val pagerState = rememberPagerState(pageCount = { activities.size })

                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 40.dp),
                        pageSpacing = 10.dp,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                ).absoluteValue

                        val scale = 1f - (0.1f * pageOffset.coerceIn(0f, 1f))
                        val alpha = 1f - (0.5f * pageOffset.coerceIn(0f, 1f))

                        ActivityCard(
                            activity = activities[page],
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(500.dp)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            TopBar(
                leftAction = onNavigateBack,
                rightAction = onNavigateHome,
                leftIcon = Res.drawable.arrow_back,
                rightIcon = Res.drawable.home,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = paddingValues.calculateTopPadding())
                    .align(Alignment.TopCenter)
                    .onGloballyPositioned { coordinates ->
                        sourceHeight = with(localDensity) { coordinates.size.height.toDp() }
                    }
            )
        }
    }
}

@Composable
private fun CardStepContent(
    content: CardContent,
    step: Int,
    totalSteps: Int,
    onButtonClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    buttonText: String,
    headerText: String,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = headerText,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .heightIn(min = 24.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )

            if (onBackClick != null && totalSteps > 2) {
                val iconColor = MaterialTheme.colorScheme.onBackground
                IconButton(
                    onClick = onBackClick,
                    enabled = enabled,
                    modifier = Modifier.size(30.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = iconColor,
                        disabledContentColor = iconColor
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = "Back",
                    )
                }
            }
        }

        Text(
            text = content.title,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = content.description,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Start,
            modifier = if (content.additional == null)
                Modifier.weight(1f)
            else
                Modifier
        )

        content.additional?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFAEAEAC),
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }

        if (content.image != null) {
            Image(
                painter = painterResource(content.image),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            )
        }

        val containerColor = if (0 == step || step == totalSteps - 1)
            MaterialTheme.colorScheme.onBackground
        else
            MaterialTheme.colorScheme.tertiary

        val contentColor = if (0 == step || step == totalSteps - 1)
            MaterialTheme.colorScheme.background
        else
            MaterialTheme.colorScheme.onBackground

        Button(
            onClick = onButtonClick,
            enabled = enabled,
            modifier = Modifier
                .height(35.dp)
                .widthIn(min = 200.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = containerColor,
                disabledContentColor = contentColor
            )
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ActivityCard(
    activity: RestActivity,
    modifier: Modifier = Modifier
) {
    
    var rotationIndex by remember(activity) { mutableStateOf(0) }
    
    
    var stepMap by remember(activity) { mutableStateOf(mapOf(0 to 0)) }

    
    val rotation by animateFloatAsState(
        targetValue = rotationIndex * 180f,
        animationSpec = tween(500)
    )

    val isAnimating = (rotation - rotationIndex * 180f).absoluteValue > 1f

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 10f * density
            },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        )
    ) {
        
        val normalizedRotation = (rotation.absoluteValue % 360)
        val isShowingBack = normalizedRotation > 90f && normalizedRotation < 270f

        
        
        val currentRotIndex = ((rotation + 90f) / 180f).toInt()

        
        val visibleStepIndex = stepMap[currentRotIndex] ?: 0
        val content = activity.steps.getOrElse(visibleStepIndex) { activity.steps[0] }

        
        val contentRotationY = if (isShowingBack) 180f else 0f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = contentRotationY
                }
        ) {
            val isStart = visibleStepIndex == 0
            val isEnd = visibleStepIndex == activity.steps.lastIndex

            val buttonText = when {
                isStart -> "Открыть инструкцию"
                isEnd -> "Готово!"
                else -> "Далее"
            }

            val onButtonClick: () -> Unit = {
                AnalyticsLogger.logEvent("rest_activity_button_clicked", mapOf(
                    "activity" to activity.name,
                    "step" to visibleStepIndex,
                    "activity_step" to "${activity.name}_step_$visibleStepIndex"
                ))
                val nextRot = if (isEnd) rotationIndex - 1 else rotationIndex + 1
                val nextStep = if (isEnd) 0 else visibleStepIndex + 1

                stepMap = stepMap + (nextRot to nextStep)
                rotationIndex = nextRot
            }

            val onBackClick: (() -> Unit)? = if (!isStart) {
                {
                    AnalyticsLogger.logEvent("rest_activity_back_clicked", mapOf(
                        "activity" to activity.name,
                        "step" to visibleStepIndex,
                        "activity_step" to "${activity.name}_step_$visibleStepIndex"
                    ))
                    val prevRot = rotationIndex - 1
                    val prevStep = visibleStepIndex - 1

                    stepMap = stepMap + (prevRot to prevStep)
                    rotationIndex = prevRot
                }
            } else null

            val headerText = 
                "ДЛИТЕЛЬНОСТЬ: ${activity.durationMin}-${activity.durationMax} минут"
            

            CardStepContent(
                content = content,
                step = visibleStepIndex,
                totalSteps = activity.steps.size,
                onButtonClick = onButtonClick,
                onBackClick = onBackClick,
                buttonText = buttonText,
                headerText = headerText,
                enabled = !isAnimating
            )
        }
    }
}