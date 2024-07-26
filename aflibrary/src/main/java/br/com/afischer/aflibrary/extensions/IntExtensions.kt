package br.com.afischer.aflibrary.extensions

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import br.com.afischer.aflibrary.AFApp
import java.util.Random


fun Int.asDrawable(): Drawable? = ResourcesCompat.getDrawable(AFApp().resources, this, null)
fun Int.asColor(): Int = ResourcesCompat.getColor(AFApp().resources, this, null)
fun Int.asString(): String = AFApp().getString(this)




fun Int.pxToDp() = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.dp() = this.dpToPx()
fun Int.px() = this.pxToDp()
val Int.dp: Int
        get() = this.dpToPx()
val Int.px: Int
        get() = this.pxToDp()


fun Int.sec(): Int = this * 1000
fun Int.min(): Int = this * 60 * 1000
fun Int.h(): Int = this * 60 * 60 * 1000
val Int.sec: Int
        get() = this * 1000
val Int.min: Int
        get() = this * 60 * 1000
val Int.h: Int
        get() = this * 60 * 60 * 1000


fun Int.minusPercent(value: Int): Double = (this * (1 - (value / 100))).toDouble()
fun Int.plusPercent(value: Int): Double = (this * (1 + (value / 100))).toDouble()
fun Int.percent(value: Int): Double = (this * value / 100).toDouble()


fun Int.pad(format: String = "<0"): String {
        val length = format.length - 1
        val char = format[1]
        val direction = format[0]
        
        return when (direction) {
                '>' -> this.toString().padEnd(length, char)
                '<' -> this.toString().padStart(length, char)
                else -> this.toString().padStart(length, char)
        }
}



fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start