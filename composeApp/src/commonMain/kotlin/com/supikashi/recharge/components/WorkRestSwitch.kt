package com.supikashi.recharge.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.supikashi.recharge.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WorkRestSwitch(
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    cornerRadius: Dp = 20.dp,
    isWork: Boolean = true,
    onChanged: (Boolean) -> Unit = {}
) {
    BoxWithConstraints(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        val totalWidth = maxWidth
        val halfWidth = totalWidth / 2f

        val indicatorOffset by animateDpAsState(if (isWork) 0.dp else halfWidth)

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(halfWidth)
                .fillMaxHeight()
                .zIndex(0f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(MaterialTheme.colorScheme.onBackground)
            )
        }

        Row(modifier = Modifier.fillMaxSize().zIndex(1f)) {
            Box(
                modifier = Modifier
                    .width(halfWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(cornerRadius))
                    .clickable {
                        onChanged(true)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "работа",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isWork) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                )
            }

            Box(
                modifier = Modifier
                    .width(halfWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(cornerRadius))
                    .clickable {
                        onChanged(false)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "отдых",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (!isWork) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkRestSwitchPreview() {
    AppTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WorkRestSwitch(
                    modifier = Modifier.width(220.dp),
                    isWork = false
                )
            }
        }
    }
}
