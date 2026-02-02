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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.theme.Background
import com.supikashi.recharge.theme.Primary
import com.supikashi.recharge.theme.Secondary
import com.supikashi.recharge.viewmodels.OnboardingState
import com.supikashi.recharge.viewmodels.OnboardingViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.clock
import recharge.composeapp.generated.resources.frame_1_png
import recharge.composeapp.generated.resources.frame_2_png
import recharge.composeapp.generated.resources.frame_3_png
import recharge.composeapp.generated.resources.onboarding_mascot_1
import recharge.composeapp.generated.resources.onboarding_mascot_2

data class OnboardingPage(
    val title: String,
    val description: String,
    val buttonString: String,
    val image: DrawableResource
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "ПРИВЕТ!",
        description = "Если ты зашел сюда, значит, ты хочешь научиться классно отдыхать!",
        buttonString = "Продолжить",
        image = Res.drawable.onboarding_mascot_1
    ),
    OnboardingPage(
        title = "МЫ\nНАПОМИНАЕМ\nО ПАУЗАХ!",
        description = "Поможем тебе сохранять фокус и энергию, встраивая короткие перерывы в твой день.",
        buttonString = "А что еще?",
        image = Res.drawable.onboarding_mascot_2
    ),
    OnboardingPage(
        title = "",
        description = "С помощью приложения ты можешь планировать своё время и находить идеи для отдыха, а мы будем тебе помогать отдыхать, присылая напоминания о перерыве!",
        buttonString = "Погнали",
        image = Res.drawable.onboarding_mascot_1
    )
)

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit
) {
    val viewModel: OnboardingViewModel = koinViewModel()
    val onboardingState by viewModel.onboardingState.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()

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
                    onNextClick = { viewModel.nextPage() }
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
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Icon(
            painter = painterResource(page.image),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "${currentPage + 1} из $totalPages",
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
                text = page.buttonString,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
