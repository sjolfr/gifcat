package uk.co.gifcat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform