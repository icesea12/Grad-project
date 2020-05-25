package com.example.bcon.domain

import com.example.bcon.util.Constants
import timber.log.Timber
import kotlin.math.roundToInt

sealed class BluetoothMessageType(
    open val data: String
) {
    abstract fun getSignalStrength(): Int
    abstract fun hasCriticalSignalStrength(): Boolean

    data class Gsm(
        override val data: String
    ) : BluetoothMessageType(data) {
        private fun extractSignalStrengthFromRawData() =
            data.substringBefore(",")
                .takeLast(2)
                .trim()
                .toInt()

        fun getErrorRate() = data.substringAfter(",")
            .take(2)
            .trim()
            .toInt()

        /**
         * Translates the value received from the module to the Gsm value. Returns the Gsm value, or 99
         * the value received was 99 (not known or not detectable)
         */
        override fun getSignalStrength(): Int {
            return when (val signalStrength = extractSignalStrengthFromRawData()) {
                99 -> 99
                else -> (-109 + ((signalStrength - 2) * 2))
            }
        }

        override fun hasCriticalSignalStrength(): Boolean {
            val signalStrength = getSignalStrength()
            return signalStrength == 99 ||
                    signalStrength < Constants.GSM_SIGNAL_STRENGTH_CRITICAL_THRESHOLD
        }

        override fun toString(): String {
            return "Gsm: raw data: $data, signal strength:${getSignalStrength()}, error rate:${getErrorRate()}"
        }
    }

    data class RfMeter(
        override val data: String
    ) : BluetoothMessageType(data) {
        override fun getSignalStrength(): Int {
            return data.removeSuffix("#").toInt().run {
                val x = this
                (19 - (0.1289f * x)).roundToInt()
            }
        }

        override fun hasCriticalSignalStrength(): Boolean {
            return getSignalStrength() < Constants.RF_METER_SIGNAL_STRENGTH_CRITICAL_THRESHOLD
        }

        override fun toString(): String {
            return "RfMeter: raw data: $data, RfMeter value: ${getSignalStrength()}"
        }
    }

    companion object {
        /**
         * To be called only when the data is known to be in the correct format, otherwise throws
         * exception.
         *
         * @return [createGsm] or [createRfMeter] object, depending on what format the incoming data matches
         * @throws IllegalArgumentException when the data format does not match one of the classes
         */
        fun fromRawData(data: String): BluetoothMessageType {
            Timber.d("Creating message from raw data: $data")
            return when {
                data.isValidRfMeter() -> createRfMeter(data)
                data.isValidGsm() -> createGsm(data)
                else -> {
                    throw IllegalArgumentException("Incoming data did not match one of the subclasses")
                }
            }
        }

        private fun createGsm(data: String) = Gsm(data)

        private fun createRfMeter(data: String) = RfMeter(data)
    }
}

private const val GSM_DATA_PATTERN = "\r\nAT[+]CSQ\r\r\n[+]CSQ: [\\d]{1,2},[\\d]{2}\r\n\r\nOK\r\n#"

/**
 * @return true if the string can be converted to a [BluetoothMessageType.Gsm] object, else false
 */
fun String.isValidGsm() = matches(Regex(GSM_DATA_PATTERN))

private const val RF_METER_DATA_PATTERN = "[\\d]{1,4}[#]"

/**
 * @return true if the string can be converted to a [BluetoothMessageType.RfMeter] object, else false
 */
fun String.isValidRfMeter(): Boolean {
    return if (this.matches(Regex(RF_METER_DATA_PATTERN))) {
        val intValue = this.removeSuffix("#").toInt()
        intValue in (0 until 1024)
    } else {
        false
    }
}