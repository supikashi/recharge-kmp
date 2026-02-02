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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.viewmodels.BreakNotificationViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.calendar
import recharge.composeapp.generated.resources.home

@Composable
fun BreakNotificationScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToRest: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToBreakResult: (type: String, durationMinutes: Int) -> Unit = { _, _ -> },
    viewModel: BreakNotificationViewModel = koinViewModel()
) {
    val breakDuration by viewModel.breakDuration.collectAsStateWithLifecycle(0)
    val currentBreak by viewModel.currentBreak.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                .fillMaxSize()
        ) {
            TopBar(
                rightAction = onNavigateHome,
                rightIcon = Res.drawable.home,
                modifier = Modifier.padding(horizontal = 20.dp)
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
                    .padding(20.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                when {
                    isLoading -> { }
                    currentBreak == null -> {
                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Этот перерыв уже прошел",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 40.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = onNavigateToRest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(15.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                text = "Начать отдыхать",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                    else -> {
                        Text(
                            text = "Время сделать перерыв!",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                viewModel.markBreakCompleted()
                                val duration = breakDuration
                                onNavigateToBreakResult("STARTED", duration)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                text = "Начать отдыхать",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.postponeBreak()
                                onNavigateToBreakResult("POSTPONED", 0)
                            },
                            modifier = Modifier
                                .heightIn(min = 40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text(
                                text = "Отложить перерыв",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Button(
                            onClick = {
                                onNavigateToBreakResult("CANCELLED", 0)
                            },
                            modifier = Modifier
                                .heightIn(min = 40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text(
                                text = "Отменить перерыв",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BreakNotificationScreenPreview() {
    AppTheme {
        BreakNotificationScreen()
    }
}
