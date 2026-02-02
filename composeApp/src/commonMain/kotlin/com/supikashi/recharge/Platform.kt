package com.supikashi.recharge

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform