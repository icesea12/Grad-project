package com.example.bcon.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Not a very well done test suite. Uses mutable data inside the class itself, with the
 * StringBuilder keeping track of te messages so far, which makes it awkward to test.
 */
class BluetoothServiceTest {
    private val correctStringDataGsm = "AT+CSQ\r\r\n+CSQ: 16,99\r\n\r\nOK\r\n#"
    private val correctByteArrayDataGsm = byteArrayOf(
        65, 84, 43, 67, 83, 81, 13, 13, 10, 43, 67, 83, 81, 58, 32, 49, 54, 44, 57, 57, 13, 10, 13,
        10, 79, 75, 13, 10, 35
    )
    private val correctStringDataRfMeter = "1023#"
    private val correctByteArrayDataRfMeter = correctStringDataRfMeter.toByteArray()
    private val bluetoothService = BluetoothService()

    @Test
    fun handleCorrectByteArrayToStringConversionGsm() {
        // Arrange
        val data = correctByteArrayDataGsm
        val expected = correctStringDataGsm
        // Act
        val stringRead = bluetoothService.byteArrayToString(data, data.size)
        // Assert
        assertEquals(expected, stringRead)
    }

    @Test
    fun handleCorrectByteArrayToStringConversionRfMeter() {
        // Arrange
        val data = correctByteArrayDataRfMeter
        val expected = correctStringDataRfMeter
        // Act
        val stringRead = bluetoothService.byteArrayToString(data, data.size)
        // Assert
        assertEquals(expected, stringRead)
    }

    @Test
    fun handleWrongByteArrayToStringConversion() {
        // Arrange
        val data = correctByteArrayDataGsm
        val expected = correctStringDataGsm.replace("\r", "\n")
        // Act
        val stringRead = bluetoothService.byteArrayToString(data, data.size)
        // Assert
        assertNotEquals(expected, stringRead)
    }

    @Test
    fun handleCorrectExtractDataGsm() {
        // Arrange
        val originalData =
            bluetoothService.byteArrayToString(
                correctByteArrayDataGsm,
                correctByteArrayDataGsm.size
            )
        bluetoothService.stringBuilder.clear().append(originalData)
        val expectedData = correctStringDataGsm
        // Act
        val data = bluetoothService.extractData()
        // Assert
        assertNotNull(data)
        assertEquals(expectedData, data)
    }

    @Test
    fun handleCorrectExtractDataRfMeter() {
        // Arrange
        val originalData =
            bluetoothService.byteArrayToString(
                correctByteArrayDataRfMeter,
                correctByteArrayDataRfMeter.size
            )
        bluetoothService.stringBuilder.clear().append(originalData)
        val expectedData = correctStringDataRfMeter
        // Act
        val data = bluetoothService.extractData()
        // Assert
        assertNotNull(data)
        assertEquals(expectedData, data)
    }

    @Test
    fun handleNullExtractData() {
        // Arrange
        val originalData = bluetoothService
            .byteArrayToString(correctByteArrayDataGsm, correctByteArrayDataGsm.size)
            .removeSuffix("#")
        bluetoothService.stringBuilder.clear().append(originalData)
        // Act
        val data = bluetoothService.extractData()
        // Assert
        assertNull(data)
    }

    @Test
    fun `put valid and some more, extract value, and make sure the rest is still there`() {
    }

    @Test
    fun `Handle entire chain of events with Gsm`() {
        bluetoothService.apply {
            val byteArray = correctByteArrayDataGsm
            val stringData = byteArrayToString(byteArray, byteArray.size)
            assertEquals(correctStringDataGsm, stringData)
            stringBuilder.clear().append(stringData)
            val extractedValue = extractData()
            assertNotNull(extractedValue)
            assertEquals("", stringBuilder.toString())
            extractedValue!!
            val isValid = isValidData(extractedValue)
            assertEquals(true, isValid)
        }
    }

    @Test
    fun `Handle entire chain of events with RfMeter`() {
        bluetoothService.apply {
            val byteArray = correctByteArrayDataRfMeter
            val stringData = byteArrayToString(byteArray, byteArray.size)
            assertEquals(correctStringDataRfMeter, stringData)
            stringBuilder.clear().append(stringData)
            val extractedValue = extractData()
            assertNotNull(extractedValue)
            assertEquals("", stringBuilder.toString())
            extractedValue!!
            val isValid = isValidData(extractedValue)
            assertEquals(true, isValid)
        }
    }

    @Test
    fun `Handle entire chain of events with extra wrong inputs with Gsm`() {
        bluetoothService.apply {
            val wrongData1 = byteArrayOf(15) + (correctByteArrayDataGsm)
            val correctData1 = (correctByteArrayDataGsm)
            val wrongData2 = byteArrayOf(62) + (correctByteArrayDataGsm)
            val correctData2 = byteArrayOf(74, 32) + (correctByteArrayDataGsm)
            val wrongData3 = (correctByteArrayDataGsm)
            val byteArray = wrongData1 + correctData1 + wrongData2 + correctData2 + wrongData3
            val stringData = byteArrayToString(byteArray, byteArray.size)
            stringBuilder.clear().append(stringData)
            // 1
            val extractedValue1 = extractData()
            val isValid1 = isValidData(extractedValue1!!)
            assertEquals(false, isValid1)
            // 2
            val extractedValue2 = extractData()
            val isValid2 = isValidData(extractedValue2!!)
            assertEquals(true, isValid2)
            // 3
            val extractedValue3 = extractData()
            val isValid3 = isValidData(extractedValue3!!)
            assertEquals(false, isValid3)
            // 4
            val extractedValue4 = extractData()
            val isValid4 = isValidData(extractedValue4!!)
            assertEquals(false, isValid4)
            assertEquals(false, stringBuilder.isEmpty())
            // 5
            val extractedValue5 = extractData()
            val isValid5 = isValidData(extractedValue5!!)
            assertEquals(true, isValid5)
            assertEquals(true, stringBuilder.isEmpty())
        }
    }

    @Test
    fun `Handle entire chain of events with extra wrong inputs and random #s inserted with Gsm`() {
        bluetoothService.apply {
            val results = booleanArrayOf(false, true, false, true, false, false)
            val hashTag = '#'.toByte()
            val data1f = byteArrayOf(15, 87, 45, 24) + hashTag
            val data2t = correctByteArrayDataGsm
            val data3f = byteArrayOf(62) + correctByteArrayDataGsm
            val data4t = correctByteArrayDataGsm
            val data5f = byteArrayOf(74, 32, 76, 94, 81, 42, 68, 75) + hashTag
            val data6f = byteArrayOf(
                65, 84, 43, 67, 83, 81, 13, 13, 10, 43, 67, 83, 81, 58, 32, 49, 54, 44, 57, 57, 13,
                10, 13, 10, 79, 75, 13, 10, 65, 84, 43, 67, 83, 81, 13, 13, 10, 43, 67, 83, 81, 58,
                32, 49, 54, 44, 57, 57, 13, 10, 13, 10, 79, 75, 13, 10
            ) + correctByteArrayDataGsm
            val byteArray = data1f + data2t + data3f + data4t + data5f + data6f
            val stringData = byteArrayToString(byteArray, byteArray.size)
            stringBuilder.clear().append(stringData)
            // 1
            val extractedValue1 = extractData()
            val isValid1 = isValidData(extractedValue1!!)
            assertEquals(results[0], isValid1)
            // 2
            val extractedValue2 = extractData()
            val isValid2 = isValidData(extractedValue2!!)
            assertEquals(results[1], isValid2)
            // 3
            val extractedValue3 = extractData()
            val isValid3 = isValidData(extractedValue3!!)
            assertEquals(results[2], isValid3)
            // 4
            val extractedValue4 = extractData()
            val isValid4 = isValidData(extractedValue4!!)
            assertEquals(results[3], isValid4)
            // 5
            val extractedValue5 = extractData()
            val isValid5 = isValidData(extractedValue5!!)
            assertEquals(results[4], isValid5)
            // 6
            val extractedValue6 = extractData()
            val isValid6 = isValidData(extractedValue6!!)
            assertEquals(results[5], isValid6)
            assertEquals(true, stringBuilder.isEmpty())
        }
    }

    @Test
    fun `Handle entire chain of events with Gsm and RfMeter combined`() {
        bluetoothService.apply {
            // Initialize
            val byteArrayGsm = correctByteArrayDataGsm
            val byteArrayRfMeter = correctByteArrayDataRfMeter
            //  Convert bytes to String
            val stringDataGsm = byteArrayToString(byteArrayGsm, byteArrayGsm.size)
            val stringDataRfMeter = byteArrayToString(byteArrayRfMeter, byteArrayRfMeter.size)
            assertEquals(correctStringDataGsm, stringDataGsm)
            assertEquals(correctStringDataRfMeter, stringDataRfMeter)
            // Append data to StringBuilder
            stringBuilder
                .clear()
                .append(stringDataGsm)
                .append(stringDataRfMeter)
            // Extract data from StringBuilder
            val extractedValueGsm = extractData()
            assertNotNull(extractedValueGsm)
            assertNotEquals("", stringBuilder.toString())
            extractedValueGsm!!
            val extractedValueRfMeter = extractData()
            assertNotNull(extractedValueRfMeter)
            assertEquals("", stringBuilder.toString())
            extractedValueRfMeter!!
            // Check validity from data taken from StringBuilder
            val isValidGsm = isValidData(extractedValueGsm)
            assertEquals(true, isValidGsm)
            val isValidRfMeter = isValidData(extractedValueRfMeter)
            assertEquals(true, isValidRfMeter)
        }
    }
}