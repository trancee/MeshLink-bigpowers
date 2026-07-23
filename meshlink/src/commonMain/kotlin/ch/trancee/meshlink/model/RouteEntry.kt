package ch.trancee.meshlink.model

import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Route table entry for multi-hop mesh routing.
 *
 * Stores destination, next hop, sequence number, and link quality metric. Sequence number is
 * sourced by destination (per RFC 8966).
 */
data class RouteEntry(
    val destination: PeerId,
    val nextHop: PeerId,
    val seqNo: UInt,
    val metric: UInt,
    val expiresAt: kotlin.time.Instant = Clock.System.now() + 30.minutes,
    val isFeasible: Boolean = true,
) {
    /** Returns true if the route has not expired. */
    fun isExpired(): Boolean {
        return Clock.System.now() > expiresAt
    }

    /** Returns the link quality metric component (RSSI portion). */
    val rssi: UInt
        get() = metric and 0xFFu

    /** Returns true if the route is reachable (metric > 0). */
    val isReachable: Boolean
        get() = metric > 0u

    override fun toString(): String {
        return "RouteEntry(dst=${destination.diagnosticId}, next=${nextHop.diagnosticId}, seq=$seqNo, metric=$metric)"
    }
}
