package com.supikashi.recharge

import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import android.os.LocaleList
import java.util.Locale

actual object LocalAppLocale {
    @Composable
    actual infix fun provides(value: String?): Array<ProvidedValue<*>> {
        val currentConfiguration = LocalConfiguration.current
        
        val newLocale = if (value == null) {
            Resources.getSystem().configuration.locales.get(0)
        } else {
            Locale(value)
        }

        Locale.setDefault(newLocale)

        val newConfiguration = Configuration(currentConfiguration).apply {
            setLocales(LocaleList(newLocale))
        }

        return arrayOf(
            LocalConfiguration provides newConfiguration
        )
    }
}
