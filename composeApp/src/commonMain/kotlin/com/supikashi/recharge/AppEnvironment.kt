package com.supikashi.recharge

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.key

expect object LocalAppLocale {
    val current: String
        @Composable get

    @Composable
    infix fun provides(value: String?): Array<ProvidedValue<*>>
}

@Composable
fun AppEnvironment(
    appLocale: String?,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        *LocalAppLocale.provides(appLocale)
    ) {
        key(appLocale) {
            content()
        }
    }
}
