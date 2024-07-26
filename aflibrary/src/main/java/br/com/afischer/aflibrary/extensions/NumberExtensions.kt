package br.com.afischer.aflibrary.extensions


import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun Double.asCurrency(symbol: String, groupPattern: String): String {
        return "$symbol ${DecimalFormat(groupPattern, DecimalFormatSymbols(Locale.getDefault())).format(this)}"
}

fun Double.asNumber(groupPattern: String): String {
        return DecimalFormat(groupPattern, DecimalFormatSymbols(Locale.getDefault())).format(this)
}

fun Int.asNumber(groupPattern: String): String {
        return DecimalFormat(groupPattern, DecimalFormatSymbols(Locale.getDefault())).format(this)
}

fun Long.asNumber(groupPattern: String): String {
        return DecimalFormat(groupPattern, DecimalFormatSymbols(Locale.getDefault())).format(this)
}



/**
 * double extensions
 */
fun Double.asCoin(locale: Locale = Locale.getDefault()): String {
        val df = DecimalFormat.getCurrencyInstance(locale)
        val cc = df.currency?.currencyCode ?: ""
        return df.format(this).replace(cc, "")
}
fun Double.asPercentage(decimals: Int = 2): String {
        val fmt = NumberFormat.getPercentInstance(Locale.getDefault()).apply {
                minimumFractionDigits = decimals
        }
        return fmt.format(this)
}
fun Double.videoTimeFormat(): String {
        val aux = this.toString().split(".")
        val millis = aux[1].take(3).padEnd(3, '0').toInt()
        var seconds = aux[0].toInt()
        val hours = seconds / (60 * 60)
        seconds %= (60 * 60)

        val minutes = seconds / 60
        seconds %= 60

        return "%02d:%02d:%02d.%03d".format(hours, minutes, seconds, millis)
}



/**
 * float extensions
 */
fun Float.asCoin(locale: Locale = Locale.getDefault()): String {
        val df = DecimalFormat.getCurrencyInstance(locale)
        val cc = df.currency?.currencyCode ?: ""
        return df.format(this).replace(cc, "")
}






fun Long.humanReadableBytes(locale: Locale = Locale.getDefault(), showUnit: Boolean = true) = when {
        this < 1024 -> "$this B"
        else -> {
                val z = (63 - this.countLeadingZeroBits()) / 10
                val value = this.toDouble() / (1L shl z * 10)
                val unit = if (showUnit) {
                        " %siB".format(" KMGTPE"[z])
                } else {
                        ""
                }

                "%.2f%s".format(locale, value, unit)
        }
}


fun Long.pad(format: String = "<0"): String {
        val length = format.length - 1
        val char = format[1]
        val direction = format[0]

        return when (direction) {
                '>' -> this.toString().padEnd(length, char)
                '<' -> this.toString().padStart(length, char)
                else -> this.toString().padStart(length, char)
        }
}

fun Long.humanReadableTime(): String {
        var time = this * 1000L

        val days: Long = TimeUnit.MILLISECONDS.toDays(time)
        time -= TimeUnit.DAYS.toMillis(days)

        val hours: Long = TimeUnit.MILLISECONDS.toHours(time)
        time -= TimeUnit.HOURS.toMillis(hours)

        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(time)
        time -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(time)


        return when {
                days > 0 -> "${days.pad("<00")}d ${hours.pad("<00")}h ${minutes.pad("<00")}min ${seconds.pad("<00")}s"
                hours > 0 -> "${hours.pad("<00")}h ${minutes.pad("<00")}min ${seconds.pad("<00")}s"
                minutes > 0 -> "${minutes.pad("<00")}min ${seconds.pad("<00")}s"
                else -> "${seconds.pad("<00")}s"
        }
}


fun Int.humanReadableTime() = this.toLong().humanReadableTime()


fun Int.humanReadableBytes(locale: Locale = Locale.getDefault(), showUnit: Boolean = true) =
        this.toLong().humanReadableBytes(locale, showUnit)

fun Int?.tryInt(default: Int = 0) = this ?: default

fun Int?.tryString(default: String = "0") = this?.toString() ?: default