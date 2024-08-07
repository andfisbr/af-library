package br.com.afischer.aflibrary.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.TextView


@Suppress("UNCHECKED_CAST")
inline fun <reified T: Any, R> T.getPrivateProperty(name: String): R? =
        T::class.java.getDeclaredField(name).apply { isAccessible = true }.get(this) as? R





fun Bitmap.dominantColor(): Int {
        val newBitmap = Bitmap.createScaledBitmap(this, 1, 1, true)
        val color = newBitmap.getPixel(0, 0)
        newBitmap.recycle()
        return color
}

fun Drawable.asBitmap(): Bitmap? {
        if (this is BitmapDrawable) {
                return this.bitmap
        }

        val bitmap = if (this.intrinsicWidth <= 0 || this.intrinsicHeight <= 0) {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
                Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)

        return bitmap
}


fun TextView.linkfy(links: MutableList<String>, clickableSpans: MutableList<ClickableSpan>) {
        val spannableString = SpannableString(this.text)

        links.forEachIndexed { i, s ->
                val clickableSpan = clickableSpans[i]

                val startIndexOfLink = this.text.toString().indexOf(s)
                if (startIndexOfLink < 0) {
                        return@forEachIndexed
                }
                spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        this.highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
        this.movementMethod = LinkMovementMethod.getInstance()
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
}



fun typeCheck(value: Any?) = when (value) {
        null -> "null"
        is String -> if (value.matches("""^\{.*\}$""".toRegex())) {
                value
        } else {
                "\"$value\""
        }
        is Number,
        is Boolean -> value
        is Map<*, *> -> (value as Map<String, Any?>).stringfy()
        is Collection<*> -> (value as Collection<Any?>).stringfy()
        is Array<*> -> (value as Array<Any?>).stringfy()
        is Char -> "\"$value\""
        is Enum<*> -> "\"$value\""
        is Pair<*, *> -> {
                val pair = mapOf(
                        "first" to value.first,
                        "second" to value.second
                )
                pair.stringfy()
        }
        is Triple<*, *, *> -> {
                val triple = mapOf(
                        "first" to value.first,
                        "second" to value.second,
                        "third" to value.third
                )
                triple.stringfy()
        }
        else -> throw IllegalArgumentException("Unsupported type: ${value.javaClass}")
}