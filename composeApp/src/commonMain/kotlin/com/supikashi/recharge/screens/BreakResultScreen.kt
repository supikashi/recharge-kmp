package com.supikashi.recharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.navigation.BreakResultType
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.theme.mascotPrimary
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.calendar
import recharge.composeapp.generated.resources.cancel_break_icon
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.postpone_break_icon

@Composable
fun BreakResultScreen(
    resultType: BreakResultType,
    durationMinutes: Int = 0,
    onNavigateHome: () -> Unit = {},
    onNavigateToRest: () -> Unit = {}
) {
    val titleText = when (resultType) {
        BreakResultType.CANCELLED -> "Перерыв\nотменен"
        BreakResultType.POSTPONED -> "Перерыв\nотложен"
        BreakResultType.STARTED -> "Перерыв\n$durationMinutes минут!"
    }

    val icon = when (resultType) {
        BreakResultType.CANCELLED -> Res.drawable.cancel_break_icon
        BreakResultType.POSTPONED -> Res.drawable.postpone_break_icon
        BreakResultType.STARTED -> Res.drawable.cancel_break_icon
    }

    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (resultType != BreakResultType.STARTED) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(55.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.height(5.dp))
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Spacer(Modifier.height(5.dp))
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
                    .size(238.dp)
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onNavigateHome,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.home),
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
            if (resultType == BreakResultType.STARTED) {
                Button(
                    onClick = onNavigateToRest,
                    modifier = Modifier.heightIn(min = 40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        text = "Чем заняться?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Spacer(Modifier.height(5.dp))
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.rotate(180f)
                )
                Spacer(Modifier.height(5.dp))
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(55.dp).rotate(180f),
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

@Preview()
@Composable
fun BreakResultScreenPreview() {
    AppTheme {
        BreakResultScreen(
            resultType = BreakResultType.STARTED
        )
    }
}
