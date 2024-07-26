package br.com.afischer.afextensions.extensions

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale


val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")


fun LocalDateTime.formatter(format: String = "dd/MM/yyyy HH:mm:ss", locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern(format, locale)
        return this.format(formatter)
}

fun LocalDateTime.getDayFromDayOfWeek(dayOfWeek: DayOfWeek): LocalDateTime {
        return this.with(TemporalAdjusters.previousOrSame(dayOfWeek))
}

fun LocalDate.getDayFromDayOfWeek(dayOfWeek: DayOfWeek): LocalDate {
        return this.with(TemporalAdjusters.previousOrSame(dayOfWeek))
}