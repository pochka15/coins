package pw.coins.db

import java.util.*

class UUIDParseException : RuntimeException("Couldn't parse UUID")

fun parseUUID(value: String): UUID {
    try {
        return UUID.fromString(value)
    } catch (e: IllegalArgumentException) {
        throw UUIDParseException()
    }
}
