package com.yumaoem.core.utils.qr_validator

object QR_Patterns {
    const val BATTERY_CODE_SEPARATOR = "-"
    const val BATTERY_BIN_INPUT_LEN: Int = 16
    const val INVERTED_BATTERY_BIN_INPUT_LEN: Int = 15


    const val BATTERY_QR_PATTERN: String = "[A-Za-z][A-Za-z][0-9]{6}$"

    const val DEX_BATTERY_PATTERN: String = "([Ee][Tt|Xx][0-9]{6})|([Ii][Vv][0-9]{6})|([Yy][Aa][0-9]{6})|([Ii][Ff][0-9]{6})$"
}