package ch.trancee.meshlink.model

/**
 * Link quality metric for BLE mesh routing.
 *
 * Combines normalized RSSI (0-255) with capability flags into a composite value. Bit layout of
 * composite:
 * - Bits 0-7: RSSI normalized (0-255)
 * - Bit 8: CoC (Connection-Oriented Channel) support
 * - Bit 9: Fast interval support
 * - Bit 10: High power tier flag
 */
data class LinkMetric(
    val rssiNormalized: UInt,
    val supportsCoc: Boolean = false,
    val fastInterval: Boolean = false,
    val highPowerTier: Boolean = false,
) {
    init {
        require(rssiNormalized <= 255u) {
            "RSSI normalized must be in range 0-255, got $rssiNormalized"
        }
    }

    /** Composite metric combining RSSI and flags. Used for route selection and comparison. */
    val composite: UInt
        get() {
            var result = rssiNormalized
            if (supportsCoc) result = result or (1u shl 8)
            if (fastInterval) result = result or (1u shl 9)
            if (highPowerTier) result = result or (1u shl 10)
            return result
        }

    /**
     * Returns true if the route is considered reachable. Infinite metric (composite == 0) means
     * unreachable.
     */
    val isReachable: Boolean
        get() = rssiNormalized > 0u

    override fun toString(): String {
        return "LinkMetric(rssi=$rssiNormalized, coc=$supportsCoc, fast=$fastInterval, highPower=$highPowerTier)"
    }
}
