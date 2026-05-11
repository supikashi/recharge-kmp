package com.supikashi.recharge.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.theme.Background
import com.supikashi.recharge.theme.Primary
import com.supikashi.recharge.theme.Secondary
import com.supikashi.recharge.viewmodels.OnboardingState
import com.supikashi.recharge.viewmodels.OnboardingViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.clock
import recharge.composeapp.generated.resources.frame_1_png
import recharge.composeapp.generated.resources.frame_2_png
import recharge.composeapp.generated.resources.frame_3_png
import recharge.composeapp.generated.resources.onboarding_btn_1
import recharge.composeapp.generated.resources.onboarding_btn_2
import recharge.composeapp.generated.resources.onboarding_btn_3
import recharge.composeapp.generated.resources.onboarding_desc_1
import recharge.composeapp.generated.resources.onboarding_desc_2
import recharge.composeapp.generated.resources.onboarding_desc_3
import recharge.composeapp.generated.resources.onboarding_mascot_1
import recharge.composeapp.generated.resources.onboarding_mascot_2
import recharge.composeapp.generated.resources.onboarding_out_of
import recharge.composeapp.generated.resources.onboarding_title_1
import recharge.composeapp.generated.resources.onboarding_title_2
import recharge.composeapp.generated.resources.onboarding_title_3

data class OnboardingPage(
    val title: StringResource,
    val description: StringResource,
    val buttonString: StringResource,
    val image: DrawableResource
)

val onboardingPages = listOf(
    OnboardingPage(
        title = Res.string.onboarding_title_1,
        description = Res.string.onboarding_desc_1,
        buttonString = Res.string.onboarding_btn_1,
        image = Res.drawable.onboarding_mascot_1
    ),
    OnboardingPage(
        title = Res.string.onboarding_title_2,
        description = Res.string.onboarding_desc_2,
        buttonString = Res.string.onboarding_btn_2,
        image = Res.drawable.onboarding_mascot_2
    ),
    OnboardingPage(
        title = Res.string.onboarding_title_3,
        description = Res.string.onboarding_desc_3,
        buttonString = Res.string.onboarding_btn_3,
        image = Res.drawable.onboarding_mascot_1
    )
)

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    isFromSettings: Boolean = false
) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val onboardingState by viewModel.onboardingState.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.checkOnboardingStatus(isFromSettings)
    }

    LaunchedEffect(onboardingState) {
        if (onboardingState is OnboardingState.NavigateToHome ||
            onboardingState is OnboardingState.OnboardingCompleted) {
            onNavigateToHome()
        }
    }

    val contentVisible = onboardingState is OnboardingState.ShowOnboarding ||
            onboardingState is OnboardingState.OnboardingCompleted

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            if (onboardingState is OnboardingState.OnboardingCompleted) {
                OnboardingContent(
                    currentPage = viewModel.totalPages - 1,
                    totalPages = viewModel.totalPages,
                    onNextClick = { }
                )
            } else {
                OnboardingContent(
                    currentPage = currentPage,
                    totalPages = viewModel.totalPages,
                    onNextClick = {
                        AnalyticsLogger.logEvent("onboarding_next_clicked", mapOf("page" to currentPage))
                        viewModel.nextPage()
                    }
                )
            }
        }
    }
}

@Composable
private fun OnboardingContent(
    currentPage: Int,
    totalPages: Int,
    onNextClick: () -> Unit
) {
    val page = onboardingPages[currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
            )
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            painter = painterResource(page.image),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.weight(1f, fill = false)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "${currentPage + 1} ${stringResource(Res.string.onboarding_out_of)} $totalPages",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Text(
                text = stringResource(page.buttonString),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview(heightDp = 700)
@Composable
fun OnboardingContentPreview() {
    AppTheme {
        OnboardingContent(0, 3, {})
    }
}