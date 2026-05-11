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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.navigation.BreakResultType
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.theme.mascotPrimary
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.break_result_cancelled
import recharge.composeapp.generated.resources.break_result_duration
import recharge.composeapp.generated.resources.break_result_postponed
import recharge.composeapp.generated.resources.break_result_what_to_do
import recharge.composeapp.generated.resources.calendar
import recharge.composeapp.generated.resources.cancel_break_icon
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.postpone_break_icon
import recharge.composeapp.generated.resources.smile
import recharge.composeapp.generated.resources.zigzag

@Composable
fun BreakResultScreen(
    resultType: BreakResultType,
    durationMinutes: Int = 0,
    onNavigateHome: () -> Unit = {},
    onNavigateToRest: () -> Unit = {}
) {
    when (resultType) {
        BreakResultType.CANCELLED -> CancelledBreak(
            onNavigateHome = onNavigateHome
        )
        BreakResultType.POSTPONED -> PostponedBreak(
            onNavigateHome = onNavigateHome
        )
        BreakResultType.STARTED -> StartedBreak(
            onNavigateHome = onNavigateHome,
            durationMinutes = durationMinutes,
            onNavigateToRest = onNavigateToRest
        )
    }

}

@Composable
fun StartedBreak(
    onNavigateHome: () -> Unit,
    onNavigateToRest: () -> Unit,
    durationMinutes: Int
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                .wrapContentHeight(unbounded = true)
                .padding(paddingValues)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth().align(Alignment.Center)
                    .offset(y = (-119).dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.zigzag),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.requiredSize(335.dp, 375.dp),
                )
                Icon(
                    painter = painterResource(Res.drawable.zigzag),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.requiredSize(335.dp, 375.dp),
                )
                Spacer(modifier = Modifier.height(750.dp))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth().align(Alignment.Center)
            ) {
                CircleText(
                    titleText = stringResource(Res.string.break_result_duration, durationMinutes),
                    onNavigateHome = onNavigateHome
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth().align(Alignment.Center)
                    .offset(y = 135.dp)
            ) {
                Spacer(modifier = Modifier.height(750.dp))
                Button(
                    onClick = {
                        AnalyticsLogger.logEvent("break_result_what_to_do_clicked")
                        onNavigateToRest()
                    },
                    modifier = Modifier.requiredHeight(40.dp)
                        ,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        text = stringResource(Res.string.break_result_what_to_do),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Icon(
                    painter = painterResource(Res.drawable.zigzag),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.requiredSize(335.dp, 375.dp)
                        .rotate(180f),
                )
                Icon(
                    painter = painterResource(Res.drawable.zigzag),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.requiredSize(335.dp, 375.dp)
                        .rotate(180f),
                )
            }
            Icon(
                painter = painterResource(Res.drawable.smile),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.align(Alignment.Center)
                    .offset(y = (-119).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}

@Composable
fun PostponedBreak(
    onNavigateHome: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Icon(
                painter = painterResource(Res.drawable.postpone_break_icon),
                contentDescription = null,
                modifier = Modifier.size(55.dp),
                tint = Color.Unspecified
            )
            Spacer(Modifier.height(5.dp))
            Icon(
                painter = painterResource(Res.drawable.postpone_break_icon),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(Modifier.height(5.dp))
            CircleText(
                titleText = stringResource(Res.string.break_result_postponed),
                onNavigateHome = onNavigateHome
            )
            Spacer(Modifier.height(5.dp))
            Icon(
                painter = painterResource(Res.drawable.postpone_break_icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.rotate(180f)
            )
            Spacer(Modifier.height(5.dp))
            Icon(
                painter = painterResource(Res.drawable.postpone_break_icon),
                contentDescription = null,
                modifier = Modifier.size(55.dp).rotate(180f),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
fun CancelledBreak(
    onNavigateHome: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Icon(
                painter = painterResource(Res.drawable.cancel_break_icon),
                contentDescription = null,
                modifier = Modifier.size(55.dp),
                tint = Color.Unspecified
            )
            Spacer(Modifier.height(5.dp))
            Icon(
                painter = painterResource(Res.drawable.cancel_break_icon),
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(Modifier.height(5.dp))
            CircleText(
                titleText = stringResource(Res.string.break_result_cancelled),
                onNavigateHome = onNavigateHome
            )
            Spacer(Modifier.height(5.dp))
            Icon(
                painter = painterResource(Res.drawable.cancel_break_icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.rotate(180f)
            )
            Spacer(Modifier.height(5.dp))
            Icon(
                painter = painterResource(Res.drawable.cancel_break_icon),
                contentDescription = null,
                modifier = Modifier.size(55.dp).rotate(180f),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
fun CircleText(
    titleText: String,
    onNavigateHome: () -> Unit
) {
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
                modifier = Modifier.weight(1.2f),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        AnalyticsLogger.logEvent("break_result_home_clicked")
                        onNavigateHome()
                    },
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = null,
                    )
                }
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
