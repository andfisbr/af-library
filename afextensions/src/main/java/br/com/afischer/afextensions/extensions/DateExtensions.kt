package br.com.afischer.afextensions.extensions

import java.text.SimpleDateFormat
import java.util.*



internal val calendar: Calendar by lazy {
        Calendar.getInstance()
}

val ptBR = Locale("pt", "BR")

val Date.today: Date
        get() = calendar.time






fun Date.asLong(): Long = this.time


fun Date.asMillis(): Long = this.asLong()


fun Date.asString(format: String = "dd/MM/yyyy HH:mm:ss"): String = SimpleDateFormat(format, ptBR).format(this)


fun Date.asTimeStamp(): Long = this.asLong()


fun Date.addDays(days: Int = 0): Date {
        calendar.time = this
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return calendar.time
}


fun Date.year(format: String = "yyyy"): Int = SimpleDateFormat(format, ptBR).format(this).toInt()
fun Date.month(): Int = SimpleDateFormat("MM", ptBR).format(this).toInt()
fun Date.day(): Int = SimpleDateFormat("dd", ptBR).format(this).toInt()
fun Date.hour(): Int = SimpleDateFormat("HH", ptBR).format(this).toInt()
fun Date.min(): Int = SimpleDateFormat("mm", ptBR).format(this).toInt()
fun Date.sec(): Int = SimpleDateFormat("ss", ptBR).format(this).toInt()





fun Long.yearsToMilliseconds(): Long = this * 365 * 24 * 60 * 60 * 1000
fun Long.monthsToMilliseconds(): Long = this * 30 * 24 * 60 * 60 * 1000
fun Long.daysToMilliseconds(): Long = this * 24 * 60 * 60 * 1000
fun Long.hoursToMilliseconds(): Long = this * 60 * 60 * 1000
fun Long.minutesToMilliseconds(): Long = this * 60 * 1000
fun Long.secondsToMilliseconds(): Long = this * 1000




val Int.year: Long
        get() = this * 365 * 24 * 60 * 60 * 1000L
val Int.years: Long
        get() = this.year

val Int.day: Long
        get() = this * 24 * 60 * 60 * 1000L
val Int.days: Long
        get() = this.day

val Int.hour: Long
        get() = this * 60 * 60 * 1000L
val Int.hours: Long
        get() = this.hour

val Int.minute: Long
        get() = this * 60 * 1000L
val Int.minutes: Long
        get() = this.minute

val Int.second: Long
        get() = this * 1000L
val Int.seconds: Long
        get() = this.second






fun Date.last(dayOfWeek: Int): Date {
        calendar.time = this

        if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return calendar.time.addDays(-7)
        }

        val offset = ((calendar.get(Calendar.DAY_OF_WEEK) - dayOfWeek) + 7) % 7
        return calendar.time.addDays(-offset)
}

fun Date.next(dayOfWeek: Int): Date {
        calendar.time = this

        if (calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return calendar.time.addDays(7)
        }

        while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
                calendar.time.addDays(1)
        }

        return calendar.time
}