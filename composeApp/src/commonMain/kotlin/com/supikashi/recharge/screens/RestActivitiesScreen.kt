package com.supikashi.recharge.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.LocalAppLocale
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.models.CardContent
import com.supikashi.recharge.models.RestActivity
import com.supikashi.recharge.models.RestType
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.viewmodels.RestActivitiesViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.active_1_1
import recharge.composeapp.generated.resources.active_1_2
import recharge.composeapp.generated.resources.active_1_3
import recharge.composeapp.generated.resources.active_1_4
import recharge.composeapp.generated.resources.active_2_1
import recharge.composeapp.generated.resources.active_2_2
import recharge.composeapp.generated.resources.active_3_1
import recharge.composeapp.generated.resources.active_3_2
import recharge.composeapp.generated.resources.active_3_3
import recharge.composeapp.generated.resources.active_4_1
import recharge.composeapp.generated.resources.active_4_2
import recharge.composeapp.generated.resources.active_4_3
import recharge.composeapp.generated.resources.active_5_1
import recharge.composeapp.generated.resources.active_5_2
import recharge.composeapp.generated.resources.active_5_3
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.list
import recharge.composeapp.generated.resources.rest_activities_all_viewed
import recharge.composeapp.generated.resources.rest_activities_done
import recharge.composeapp.generated.resources.rest_activities_duration
import recharge.composeapp.generated.resources.rest_activities_load_error
import recharge.composeapp.generated.resources.rest_activities_loading
import recharge.composeapp.generated.resources.rest_activities_next
import recharge.composeapp.generated.resources.rest_activities_open_instruction
import recharge.composeapp.generated.resources.rest_activities_restart
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RestActivitiesScreen(
    type: RestType,
    onNavigateToList: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RestActivitiesViewModel = koinViewModel()
) {
    val localDensity = LocalDensity.current
    val locale = LocalAppLocale.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var sourceHeight by remember { mutableStateOf(0.dp) }
    val shuffledActivities = remember(type, locale, uiState.activities) { uiState.activities.shuffled() }
    var currentIndex by rememberSaveable(type, locale) { mutableStateOf(0) }

    LaunchedEffect(type, locale) {
        viewModel.loadActivities(type, locale)
    }

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

                BoxWithConstraints(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    val screenWidth = constraints.maxWidth.toFloat()

                    when {
                        uiState.isLoading -> RestActivitiesMessage(
                            text = stringResource(Res.string.rest_activities_loading)
                        )

                        uiState.isError -> RestActivitiesMessage(
                            text = stringResource(Res.string.rest_activities_load_error)
                        )

                        else -> Crossfade(
                            targetState = currentIndex < shuffledActivities.size,
                            modifier = Modifier.fillMaxSize(),
                            label = "activities_crossfade"
                        ) { hasMoreActivities ->
                            if (hasMoreActivities) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    val safeIndex = currentIndex.coerceAtMost(shuffledActivities.size - 1)
                                    if (safeIndex >= 0) {
                                        val activity = shuffledActivities[safeIndex]
                                        val offsetX = remember(safeIndex) { Animatable(0f) }
                                        val rotation = remember(safeIndex) { Animatable(0f) }
                                        val coroutineScope = rememberCoroutineScope()

                                        if (safeIndex + 1 < shuffledActivities.size) {
                                            val nextActivity = shuffledActivities[safeIndex + 1]
                                            key(nextActivity) {
                                                ActivityCard(
                                                    activity = nextActivity,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 40.dp)
                                                        .height(500.dp)
                                                        .graphicsLayer {
                                                            val progress = (offsetX.value.absoluteValue / (screenWidth * 1.5f)).coerceIn(0f, 1f)
                                                            scaleX = 0.9f + (0.1f * progress)
                                                            scaleY = 0.9f + (0.1f * progress)
                                                            alpha = progress
                                                        }
                                                )
                                            }
                                        }

                                        key(activity) {
                                            ActivityCard(
                                                activity = activity,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 40.dp)
                                                    .height(500.dp)
                                                    .graphicsLayer {
                                                        translationX = offsetX.value
                                                        rotationZ = rotation.value
                                                    }
                                                    .pointerInput(safeIndex) {
                                                        detectDragGestures(
                                                            onDragEnd = {
                                                                coroutineScope.launch {
                                                                    if (offsetX.value.absoluteValue > screenWidth / 4f) {
                                                                        val targetX = if (offsetX.value > 0) screenWidth * 1.5f else -screenWidth * 1.5f
                                                                        val jobX = launch { offsetX.animateTo(targetX, tween(300)) }
                                                                        val jobRot = launch { rotation.animateTo(rotation.value + if (offsetX.value > 0) 20f else -20f, tween(300)) }
                                                                        jobX.join()
                                                                        jobRot.join()
                                                                        currentIndex++
                                                                    } else {
                                                                        launch { offsetX.animateTo(0f, tween(300)) }
                                                                        launch { rotation.animateTo(0f, tween(300)) }
                                                                    }
                                                                }
                                                            },
                                                            onDrag = { change, dragAmount ->
                                                                change.consume()
                                                                coroutineScope.launch {
                                                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                                                    rotation.snapTo(offsetX.value / 20f)
                                                                }
                                                            }
                                                        )
                                                    }
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.rest_activities_all_viewed),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 20.dp)
                                        )
                                        Spacer(Modifier.height(20.dp))
                                        Button(
                                            onClick = { currentIndex = 0 },
                                            colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.onBackground, contentColor = MaterialTheme.colorScheme.background)
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.rest_activities_restart),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            TopBar(
                leftAction = onNavigateBack,
                rightAction = onNavigateToList,
                leftIcon = Res.drawable.arrow_back,
                rightIcon = Res.drawable.list,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RestActivitiesListScreen(
    type: RestType,
    onNavigateToCardView: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RestActivitiesViewModel = koinViewModel()
) {
    val localDensity = LocalDensity.current
    val locale = LocalAppLocale.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var sourceHeight by remember { mutableStateOf(0.dp) }
    val originalActivities = uiState.activities

    LaunchedEffect(type, locale) {
        viewModel.loadActivities(type, locale)
    }

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
                    when {
                        uiState.isLoading -> RestActivitiesMessage(
                            text = stringResource(Res.string.rest_activities_loading)
                        )

                        uiState.isError -> RestActivitiesMessage(
                            text = stringResource(Res.string.rest_activities_load_error)
                        )

                        originalActivities.isEmpty() -> RestActivitiesMessage(
                            text = stringResource(Res.string.rest_activities_all_viewed)
                        )

                        else -> {
                            val pagerState = rememberPagerState(pageCount = { originalActivities.size })

                            HorizontalPager(
                                state = pagerState,
                                contentPadding = PaddingValues(horizontal = 40.dp),
                                pageSpacing = 5.dp,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                ActivityCard(
                                    activity = originalActivities[page],
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(500.dp)
                                        .graphicsLayer {
                                            val pageOffset = (
                                                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                                ).absoluteValue

                                            val scale = 1f - (0.1f * pageOffset.coerceIn(0f, 1f))
                                            val cardAlpha = 1f - (0.5f * pageOffset.coerceIn(0f, 1f))

                                            scaleX = scale
                                            scaleY = scale
                                            this.alpha = cardAlpha
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            TopBar(
                leftAction = onNavigateBack,
                leftIcon = Res.drawable.arrow_back,
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
private fun RestActivitiesMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()
    )
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

        val imageResource = mapImageToResource(content.imageId)
        if (imageResource != null) {
            Image(
                painter = painterResource(imageResource),
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

private fun mapImageToResource(imageId: String?): DrawableResource? = when (imageId) {
    "active_1_1" -> Res.drawable.active_1_1
    "active_1_2" -> Res.drawable.active_1_2
    "active_1_3" -> Res.drawable.active_1_3
    "active_1_4" -> Res.drawable.active_1_4
    "active_2_1" -> Res.drawable.active_2_1
    "active_2_2" -> Res.drawable.active_2_2
    "active_3_1" -> Res.drawable.active_3_1
    "active_3_2" -> Res.drawable.active_3_2
    "active_3_3" -> Res.drawable.active_3_3
    "active_4_1" -> Res.drawable.active_4_1
    "active_4_2" -> Res.drawable.active_4_2
    "active_4_3" -> Res.drawable.active_4_3
    "active_5_1" -> Res.drawable.active_5_1
    "active_5_2" -> Res.drawable.active_5_2
    "active_5_3" -> Res.drawable.active_5_3
    else -> null
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
                isStart -> stringResource(Res.string.rest_activities_open_instruction)
                isEnd -> stringResource(Res.string.rest_activities_done)
                else -> stringResource(Res.string.rest_activities_next)
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

            val headerText = stringResource(Res.string.rest_activities_duration, activity.durationMin, activity.durationMax)
            

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
