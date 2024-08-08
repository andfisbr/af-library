package br.com.afischer.aflibrary.extensions

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import br.com.afischer.aflibrary.AFLibraryApp
import java.util.Random


fun Int.asBitmap() = BitmapFactory.decodeResource(AFLibraryApp().resources, this)
fun Int.asColor(): Int = ResourcesCompat.getColor(AFLibraryApp().resources, this, null)
fun Int.asContrastColor(): Int = this.asHexColor().asContrastColor()
fun Int.asDrawable(): Drawable? = ResourcesCompat.getDrawable(AFLibraryApp().resources, this, null)
fun Int.asHexColor(): String = String.format("#%06X", (0xFFFFFF and this.asColor()))
fun Int.asString(): String = AFLibraryApp().getString(this)




fun Int.pxToDp() = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.dpToPx() = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Int.dp() = this.dpToPx()
fun Int.px() = this.pxToDp()
val Int.dp: Int
        get() = this.dpToPx()
val Int.px: Int
        get() = this.pxToDp()




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


