package com.supikashi.recharge.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.supikashi.recharge.models.AppLanguage
import org.jetbrains.compose.resources.stringResource
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.language_en
import recharge.composeapp.generated.resources.language_ru
import recharge.composeapp.generated.resources.language_select_title
import recharge.composeapp.generated.resources.language_system

@Composable
fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.language_select_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AppLanguage.entries.forEach { language ->
                    val isSelected = language == currentLanguage
                    Button(
                        colors = if (isSelected) {
                            ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.background,
                                containerColor = MaterialTheme.colorScheme.onBackground
                            )
                        } else {
                            ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onLanguageSelected(language)
                            onDismiss()
                        }
                    ) {
                        Text(
                            text = when (language) {
                                AppLanguage.SYSTEM -> stringResource(Res.string.language_system)
                                AppLanguage.RUSSIAN -> stringResource(Res.string.language_ru)
                                AppLanguage.ENGLISH -> stringResource(Res.string.language_en)
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
