package com.yumacustomer.core_logger.api

/**
 * A simple logging API for Multiplatform .
 * This interface provides a common API for logging debug and error messages,
 * which is implemented natively on each platform (Android, iOS).
 */
interface LoggerApi {

    /**
     * Logs a debug message with a default tag.
     *
     * @param message The message to be logged.
     */
    fun logD(message: String)

    /**
     * Logs a debug message with a specified tag.
     *
     * @param tag A custom tag for the log message.
     * @param message The message to be logged.
     */
    fun logDWithTag(tag: String, message: String)

    /**
     * Logs an error message and an exception with a default tag.
     *
     * @param message The error message to be logged.
     * @param e The exception to be logged.
     */
    fun logE(message: String, e: Exception)

    /**
     * Logs an error message and an exception with a specified tag.
     *
     * @param tag A custom tag for the log message.
     * @param message The error message to be logged.
     * @param e The exception to be logged.
     */
    fun logEWithTag(tag: String, message: String, e: Exception)
}