package br.com.afischer.aflibrary.extensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt


const val DAY_IN_MILLIS = 1000L * 60L * 60L * 24L





val Calendar.atEndOfDay: Calendar
        get() = withTimeAtEndOfPeriod(24)

val Calendar.atStartOfDay: Calendar
        get() = withTimeAtStartOfPeriod(0)

val Calendar.dayOfMonth: Int
        get() = get(Calendar.DAY_OF_MONTH)

val Calendar.dayOfWeek: Int
        get() = get(Calendar.DAY_OF_WEEK)

val Calendar.daysFromToday: Int
        get() {
                val diff = (atStartOfDay.timeInMillis - today().timeInMillis).toFloat()
                return (diff / DAY_IN_MILLIS).roundToInt()
        }

var Calendar.hour: Int
        get() = get(Calendar.HOUR_OF_DAY)
        set(value) {
                set(Calendar.HOUR_OF_DAY, value)
        }

val Calendar.isBeforeToday: Boolean
        get() = isBefore(today())

val Calendar.isToday: Boolean
        get() = isSameDate(today())

val Calendar.isWeekend: Boolean
        get() = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

val Calendar.lengthOfMonth: Int
        get() = getActualMaximum(Calendar.DAY_OF_MONTH)

var Calendar.minute: Int
        get() = get(Calendar.MINUTE)
        set(value) {
                set(Calendar.MINUTE, value)
        }

val Calendar.month: Int
        get() = get(Calendar.MONTH)

val Calendar.weekOfYear: Int
        get() = get(Calendar.WEEK_OF_YEAR)

val Calendar.year: Int
        get() = get(Calendar.YEAR)










fun defaultDateFormatter(numberOfDays: Int): SimpleDateFormat = when (numberOfDays) {
        1 -> SimpleDateFormat("EEEE M/dd", Locale.getDefault()) // full weekday
        in 2..6 -> SimpleDateFormat("EEE M/dd", Locale.getDefault()) // first three characters
        else -> SimpleDateFormat("EEEEE M/dd", Locale.getDefault()) // first character
}


fun defaultTimeFormatter(): SimpleDateFormat = SimpleDateFormat("hh a", Locale.getDefault())


fun firstDayOfYear(): Calendar {
        return today().apply {
                set(Calendar.MONTH, Calendar.JANUARY)
                set(Calendar.DAY_OF_MONTH, 1)
        }
}


fun newDate(year: Int, month: Int, dayOfMonth: Int): Calendar {
        return today().apply {
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.MONTH, month)
                set(Calendar.YEAR, year)
        }
}


fun now() = Calendar.getInstance()


fun today() = now().atStartOfDay




/**
 * operations
 */

fun Calendar.isEqual(other: Calendar) = timeInMillis == other.timeInMillis


fun Calendar.isNotEqual(other: Calendar) = isEqual(other).not()


fun Calendar.addDays(days: Int) {
        add(Calendar.DATE, days)
}


fun Calendar.addHours(hours: Int) {
        add(Calendar.HOUR_OF_DAY, hours)
}


fun Calendar.addMillis(millis: Int) {
        add(Calendar.MILLISECOND, millis)
}


fun Calendar.minusDays(days: Int): Calendar {
        return copy().apply {
                add(Calendar.DATE, days * (-1))
        }
}


fun Calendar.minusHours(hours: Int): Calendar {
        return copy().apply {
                add(Calendar.HOUR_OF_DAY, hours * (-1))
        }
}


fun Calendar.minusMillis(millis: Int): Calendar {
        return copy().apply {
                add(Calendar.MILLISECOND, millis * (-1))
        }
}


fun Calendar.minusMinutes(minutes: Int): Calendar {
        return copy().apply {
                add(Calendar.MINUTE, minutes * (-1))
        }
}


fun Calendar.plusDays(days: Int): Calendar {
        return copy().apply {
                add(Calendar.DATE, days)
        }
}


fun Calendar.plusHours(hours: Int): Calendar {
        return copy().apply {
                add(Calendar.HOUR_OF_DAY, hours)
        }
}


fun Calendar.plusMillis(millis: Int): Calendar {
        return copy().apply {
                add(Calendar.MILLISECOND, millis)
        }
}


fun Calendar.plusMinutes(minutes: Int): Calendar {
        return copy().apply {
                add(Calendar.MINUTE, minutes)
        }
}


fun Calendar.subtractDays(days: Int) {
        add(Calendar.DATE, days * (-1))
}


fun Calendar.subtractHours(hours: Int) {
        add(Calendar.HOUR_OF_DAY, hours * (-1))
}


fun Calendar.subtractMillis(millis: Int) {
        add(Calendar.MILLISECOND, millis * (-1))
}


fun Calendar.subtractMinutes(minutes: Int) {
        add(Calendar.MINUTE, minutes * (-1))
}


fun Calendar.computeDifferenceWithFirstDayOfWeek(): Int {
        val firstDayOfWeek = firstDayOfWeek
        return if (firstDayOfWeek == Calendar.MONDAY && dayOfWeek == Calendar.SUNDAY) {
                // Special case, because Calendar.MONDAY has constant value 2 and Calendar.SUNDAY has
                // constant value 1. The correct result to return is 6 days, not -1 days.
                6

        } else {
                dayOfWeek - firstDayOfWeek
        }
}


fun Calendar.copy(): Calendar = clone() as Calendar


fun Calendar.firstDateOfThisWeek(): Calendar {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        return this
}


fun Calendar.format(): String {
        val sdf = SimpleDateFormat.getDateTimeInstance()
        return sdf.format(time)
}


fun Calendar.isAfter(other: Calendar) = timeInMillis > other.timeInMillis


fun Calendar.isAtEndOfPeriod(hour: Int): Boolean {
        return isEqual(withTimeAtEndOfPeriod(hour))
}


fun Calendar.isAtStartOfPeriod(hour: Int): Boolean {
        return isEqual(withTimeAtStartOfPeriod(hour))
}


fun Calendar.isBefore(other: Calendar) = timeInMillis < other.timeInMillis


fun Calendar.isSameDate(other: Calendar): Boolean = toEpochDays() == other.toEpochDays()


fun Calendar.lastDateOfThisWeek(): Calendar {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek + 6)
        return this
}


infix fun Calendar.minutesUntil(other: Calendar): Int {
        val diff = (timeInMillis - other.timeInMillis) / 60_000
        return diff.toInt()
}


fun Calendar.nextFirstDayOfWeek(): Calendar {
        val result = this.plusDays(1)
        while (result.dayOfWeek != firstDayOfWeek) {
                result.add(Calendar.DATE, 1)
        }
        return result
}


fun Calendar.previousFirstDayOfWeek(): Calendar {
        val result = this.minusDays(1)
        while (result.dayOfWeek != firstDayOfWeek) {
                result.add(Calendar.DATE, -1)
        }
        return result
}


fun Calendar.toEpochDays(): Int = (atStartOfDay.timeInMillis / DAY_IN_MILLIS).toInt()


fun Calendar.withDayOfMonth(day: Int): Calendar {
        return copy().apply { set(Calendar.DAY_OF_MONTH, day) }
}


fun Calendar.withHour(hour: Int): Calendar {
        return copy().apply { set(Calendar.HOUR_OF_DAY, hour) }
}


fun Calendar.withLocalTimeZone(): Calendar {
        val localTimeZone = TimeZone.getDefault()
        val localCalendar = Calendar.getInstance(localTimeZone)
        localCalendar.timeInMillis = timeInMillis
        return localCalendar
}


fun Calendar.withMinutes(minute: Int): Calendar {
        return copy().apply { set(Calendar.MINUTE, minute) }
}


fun Calendar.withMonth(month: Int): Calendar {
        return copy().apply { set(Calendar.MONTH, month) }
}


fun Calendar.withTime(hour: Int, minutes: Int): Calendar {
        return copy().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
        }
}


fun Calendar.withTimeAtEndOfPeriod(hour: Int): Calendar {
        return copy().apply {
                set(Calendar.HOUR_OF_DAY, hour - 1)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
        }
}


fun Calendar.withTimeAtStartOfPeriod(hour: Int): Calendar {
        return copy().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
        }
}


fun Calendar.withYear(year: Int): Calendar {
        return copy().apply { set(Calendar.YEAR, year) }
}