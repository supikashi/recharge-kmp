package com.supikashi.recharge.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun TimeRangeInputManual(
    modifier: Modifier = Modifier,
    from: String = "",
    to: String = "",
    onFromTimeChanged: (String) -> Unit = {},
    onToTimeChanged: (String) -> Unit = {}
) {
    var fromValue by remember(from) { 
        mutableStateOf(TextFieldValue(from, selection = TextRange(from.length))) 
    }
    var toValue by remember(to) { 
        mutableStateOf(TextFieldValue(to, selection = TextRange(to.length))) 
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.height(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "от",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            BasicTextField(
                value = fromValue,
                onValueChange = { newValue ->
                    val result = formatToTimeWithCursor(newValue)
                    fromValue = result
                    onFromTimeChanged(result.text)
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xFFE8E8E8), RoundedCornerShape(20.dp)),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fromValue.text.isEmpty()) {
                            Text(
                                text = "--:--",
                                style = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.height(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "до",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            BasicTextField(
                value = toValue,
                onValueChange = { newValue ->
                    val result = formatToTimeWithCursor(newValue)
                    toValue = result
                    onToTimeChanged(result.text)
                },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xFFE8E8E8), RoundedCornerShape(20.dp)),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (toValue.text.isEmpty()) {
                            Text(
                                text = "--:--",
                                style = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

private fun formatToTimeWithCursor(input: TextFieldValue): TextFieldValue {
    val digits = input.text.filter { it.isDigit() }.take(4)

    val validDigits = if (digits.length == 4) {
        val hours = digits.substring(0, 2).toInt().coerceAtMost(23)
        val hoursString = if (hours < 10) "0$hours" else hours.toString()
        val minutes = digits.substring(2, 4).toInt().coerceAtMost(59)
        val mins = if (minutes < 10) "0$minutes" else minutes.toString()
        hoursString + mins
    } else if (digits.length == 2) {
        val hours = digits.substring(0, 2).toInt().coerceAtMost(23)
        if (hours < 10) "0$hours" else hours.toString()
    } else {
        digits
    }

    val formattedText = when {
        validDigits.isEmpty() -> ""
        validDigits.length <= 2 -> validDigits
        else -> "${validDigits.substring(0, 2)}:${validDigits.substring(2)}"
    }

    val newCursorPosition = when {
        formattedText.isEmpty() -> 0
        validDigits.length <= 2 -> validDigits.length
        else -> minOf(formattedText.length, validDigits.length + 1)
    }
    
    return TextFieldValue(
        text = formattedText,
        selection = TextRange(newCursorPosition)
    )
}

@Preview(showBackground = true)
@Composable
private fun TimeRangeInputManualPreview() {
    TimeRangeInputManual(
        from = "0800",
        to = "1730"
    )
}
