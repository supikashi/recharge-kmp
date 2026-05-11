package com.supikashi.recharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.components.LanguageSelectionDialog
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.models.AppLanguage
import com.supikashi.recharge.models.RestType
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.viewmodels.SettingsViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.settings_change_pomodoro
import recharge.composeapp.generated.resources.settings_show_onboarding
import recharge.composeapp.generated.resources.settings_title
import recharge.composeapp.generated.resources.settings_language
import recharge.composeapp.generated.resources.language_system
import recharge.composeapp.generated.resources.language_ru
import recharge.composeapp.generated.resources.language_en

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPomodoroSelection: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val currentLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()

    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { newLanguage ->
                viewModel.setLanguage(newLanguage)
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                .fillMaxSize()
        ) {
            TopBar(
                leftAction = onNavigateBack,
                leftIcon = Res.drawable.arrow_back,
                rightAction = onNavigateBack,
                rightIcon = Res.drawable.home,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Text(
                text = stringResource(Res.string.settings_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SettingsButton(
                        text = stringResource(Res.string.settings_change_pomodoro),
                        onClick = {
                            AnalyticsLogger.logEvent("settings_change_pomodoro_clicked")
                            onNavigateToPomodoroSelection()
                        }
                    )

                    SettingsButton(
                        text = stringResource(Res.string.settings_language),
                        onClick = {
                            AnalyticsLogger.logEvent("settings_change_language_clicked")
                            showLanguageDialog = true
                        }
                    )

                    SettingsButton(
                        text = stringResource(Res.string.settings_show_onboarding),
                        onClick = {
                            AnalyticsLogger.logEvent("settings_show_onboarding_clicked")
                            onNavigateToOnboarding()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
