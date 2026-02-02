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
import com.supikashi.recharge.models.RestType
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.home


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestScreen(
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToType: (RestType) -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            TopBar(
                leftAction = onNavigateBack,
                rightAction = onNavigateHome,
                leftIcon = Res.drawable.arrow_back,
                rightIcon = Res.drawable.home,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            
            Text(
                text = "Выбери вид отдыха",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f).padding(horizontal = 20.dp),
                textAlign = TextAlign.Center
            )
            RestType.entries.forEach { type ->
                RestTypeItem(
                    type = type,
                    onSelect = {
                        onNavigateToType(type)
                    }
                )
            }
        }
    }
}

@Composable
private fun RestTypeItem(
    type: RestType,
    onSelect: () -> Unit
) {
    Button(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = when (type) {
                RestType.CALM -> "Спокойный"
                RestType.ACTIVE -> "Активный"
                RestType.CREATIVE -> "Творческий"
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

