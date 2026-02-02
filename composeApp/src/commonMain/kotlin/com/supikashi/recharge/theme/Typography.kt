package com.supikashi.recharge.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import recharge.composeapp.generated.resources.Gerhaus
import recharge.composeapp.generated.resources.Montserrat_Medium
import recharge.composeapp.generated.resources.Montserrat_Regular
import recharge.composeapp.generated.resources.Res

val Gerhaus @Composable get() = FontFamily(
    Font(
        resource = Res.font.Gerhaus
    )
)

val Montserrat @Composable get() = FontFamily(
    Font(
        resource = Res.font.Montserrat_Regular,
        weight = FontWeight.Normal
    ),
    Font(
        resource = Res.font.Montserrat_Medium,
        weight = FontWeight.Medium
    )
)

val Typography @Composable get() = Typography(
    headlineMedium = TextStyle(
        fontFamily = Gerhaus,
        fontSize = 36.sp,
        lineHeight = 40.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    )
)
