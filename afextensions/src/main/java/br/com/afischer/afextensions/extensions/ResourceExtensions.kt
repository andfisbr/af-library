package br.com.afischer.afextensions.extensions

import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import br.com.afischer.afextensions.AFExtApp


fun Int.str(vararg params: Any): String {
        if (params.isEmpty()) {
                return AFExtApp().getString( this, "")
        }
        return AFExtApp().getString(this, *params)
}
fun Int.colorify(): Int = ResourcesCompat.getColor(AFExtApp().resources, this, null)



fun Int.attrColor(): Int {
        val typedValue = TypedValue()
        AFExtApp().theme.resolveAttribute(this, typedValue, true)
        val arr = AFExtApp().obtainStyledAttributes(typedValue.data, intArrayOf(this))
        val color = arr.getColor(0, -1)
        arr.recycle()
        return color
}


