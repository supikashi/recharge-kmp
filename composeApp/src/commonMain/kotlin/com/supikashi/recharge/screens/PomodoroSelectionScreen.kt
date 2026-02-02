package com.supikashi.recharge.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.viewmodels.PomodoroSelectionViewModel
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroSelectionScreen(
    onPomodoroSelected: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: PomodoroSelectionViewModel = koinViewModel()
    val pomodoroTypes = viewModel.getPomodoroTypes()

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
                text = "Выбери вид\nтайм-менеджмента",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            pomodoroTypes.forEach { type ->
                PomodoroTypeItem(
                    type = type,
                    onSelect = {
                        viewModel.selectPomodoroType(type)
                        onPomodoroSelected()
                    }
                )
            }
        }
    }
}

@Composable
private fun PomodoroTypeItem(
    type: PomodoroType,
    onSelect: () -> Unit
) {
    Button(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = when (type) {
                PomodoroType.DEBUG -> "Debug type"
                PomodoroType.CLASSIC -> "Pomodoro 25/5"
                PomodoroType.EXTENDED -> "Pomodoro 52/17"
                PomodoroType.DEEP_WORK -> "Pomodoro 90/30"
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
