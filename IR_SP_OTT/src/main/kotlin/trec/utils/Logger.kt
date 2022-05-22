package trec.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    fun info(message: String) {
        log(LoggingType.Info, message)
    }


    fun error(message: String) {
        log(LoggingType.Error, message)
    }


    fun debug(message: String) {
        log(LoggingType.Debug, message)
    }

    fun warning(message: String) {
        log(LoggingType.Warning, message)
    }

    private fun log(logType: LoggingType, message: String) {
        println("${LocalDateTime.now().format(formatter)} $[$logType] - $message")
    }

}