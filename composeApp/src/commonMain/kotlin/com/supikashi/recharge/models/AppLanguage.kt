package com.supikashi.recharge.models

enum class AppLanguage {
    SYSTEM,
    RUSSIAN,
    ENGLISH;
    
    fun next(): AppLanguage {
        val values = entries.toTypedArray()
        return values[(this.ordinal + 1) % values.size]
    }
}
