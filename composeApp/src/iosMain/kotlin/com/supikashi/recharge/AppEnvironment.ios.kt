package com.supikashi.recharge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import platform.Foundation.NSUserDefaults

actual object LocalAppLocale {
    private const val LANG_KEY = "AppleLanguages"

    @Composable
    actual infix fun provides(value: String?): Array<ProvidedValue<*>> {
        if (value == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(value), LANG_KEY)
        }
        return emptyArray()
    }
}
