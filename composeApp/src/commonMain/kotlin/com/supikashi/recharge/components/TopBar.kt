package com.supikashi.recharge.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home

@Composable
fun TopBar(
    leftAction: () -> Unit = { },
    rightAction: () -> Unit = { },
    leftIcon: DrawableResource? = null,
    rightIcon: DrawableResource? = null,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            leftIcon?.let {
                IconButton(onClick = leftAction) {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            rightIcon?.let {
                IconButton(onClick = rightAction) {
                    Icon(
                        painter = painterResource(it),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Preview()
@Composable
fun TopBarPreview() {
    TopBar()
}