package dev.aurakai.auraframefx.utils

import java.time.Clock
import java.time.Duration
import java.time.Instant

/**
 * Time utilities for the AuraFrameFX system
 */
object TimeUtils {
    val systemClock: Clock = Clock.systemUTC()
    
    /**
 * Returns the current instant based on the system clock in UTC.
 *
 * @return The current point in time as an [Instant].
 */
fun now(): Instant = Instant.now(systemClock)
    
    /**
 * Returns the current system time in milliseconds since the Unix epoch.
 *
 * @return The current timestamp in milliseconds.
 */
fun currentTimestamp(): Long = System.currentTimeMillis()
    
    /**
     * Returns the duration elapsed since the specified instant until the current time.
     *
     * @param instant The starting point in time from which to measure the duration.
     * @return The duration between the given instant and now.
     */
    fun durationSince(instant: Instant): Duration {
        return Duration.between(instant, now())
    }
}

// Type aliases for common time types
typealias AuraInstant = Instant
typealias AuraClock = Clock
typealias AuraDuration = Duration
