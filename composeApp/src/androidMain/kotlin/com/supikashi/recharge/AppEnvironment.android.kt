package com.supikashi.recharge

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import android.os.LocaleList
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

actual object LocalAppLocale {
    
    private var originalSystemLocale: Locale? = null

    actual val current: String
        @Composable get() = LocalConfiguration.current.locales.get(0).language

    @Composable
    actual infix fun provides(value: String?): Array<ProvidedValue<*>> {
        val currentConfiguration = LocalConfiguration.current

        if (originalSystemLocale == null) {
            originalSystemLocale = LocalLocale.current.platformLocale
        }
        
        val newLocale = if (value == null) {
            originalSystemLocale!!
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
