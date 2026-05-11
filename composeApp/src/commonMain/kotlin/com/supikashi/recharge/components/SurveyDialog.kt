package com.supikashi.recharge.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.supikashi.recharge.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.frame_2
import recharge.composeapp.generated.resources.mid_face
import recharge.composeapp.generated.resources.sad_face
import recharge.composeapp.generated.resources.smile_face

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import com.supikashi.recharge.theme.mascotPrimary
import org.jetbrains.compose.resources.stringResource
import recharge.composeapp.generated.resources.survey_dialog_question
import recharge.composeapp.generated.resources.survey_dialog_title

@Composable
fun SurveyDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    var selectedValue by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.secondary,
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
                    text = stringResource(Res.string.survey_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(Res.string.survey_dialog_question),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val scope = rememberCoroutineScope()
                    for (i in 1..5) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedValue == i) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.background
                                )
                                .clickable {
                                    selectedValue = i
                                    scope.launch {
                                        delay(300)
                                        onSubmit(i)
                                    }
                                }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        modifier = Modifier.width(60.dp),
                        painter = painterResource(Res.drawable.sad_face),
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier.width(60.dp),
                        painter = painterResource(Res.drawable.mid_face),
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier.width(60.dp),
                        painter = painterResource(Res.drawable.smile_face),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SurveyDialogPreview() {
    AppTheme {
        SurveyDialog(onDismiss = {}, onSubmit = {})
    }
}
