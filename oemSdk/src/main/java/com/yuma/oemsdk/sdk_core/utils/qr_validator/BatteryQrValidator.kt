package com.yumaoem.core.utils.qr_validator

import com.yumaoem.core.utils.qr_validator.QR_Patterns.BATTERY_BIN_INPUT_LEN
import com.yumaoem.core.utils.qr_validator.QR_Patterns.BATTERY_CODE_SEPARATOR
import com.yumaoem.core.utils.qr_validator.QR_Patterns.BATTERY_QR_PATTERN
import com.yumaoem.core.utils.qr_validator.QR_Patterns.DEX_BATTERY_PATTERN
import com.yumaoem.core.utils.qr_validator.QR_Patterns.INVERTED_BATTERY_BIN_INPUT_LEN

object BatteryQrValidator {

    private val batteryPattern = Regex(BATTERY_QR_PATTERN,RegexOption.IGNORE_CASE)
    private val dexBatteryPattern = Regex(DEX_BATTERY_PATTERN,RegexOption.IGNORE_CASE)


    fun validateBatteryQr(input: String?): Boolean {
        if (input.isNullOrEmpty()) return false

        return if (input.contains(BATTERY_CODE_SEPARATOR)) {
            validateCombinedBatteryBin(input)
        } else {
            dexBatteryPattern.matches(input)
        }
    }

    fun validateCombinedBatteryBin(input: String): Boolean {
        val parts = input.split(BATTERY_CODE_SEPARATOR)
            .dropLastWhile { it.isEmpty() }

        if (parts.size != 2) return false

        return validateBatteryCode(parts[0]) &&
                isValidBatteryBin(parts[1])
    }

    private fun validateBatteryCode(code: String): Boolean =
        batteryPattern.matches(code) || dexBatteryPattern.matches(code)

    private fun isValidBatteryBin(input: String): Boolean =
        input.length == BATTERY_BIN_INPUT_LEN ||
                input.length == INVERTED_BATTERY_BIN_INPUT_LEN
}
