package br.com.afischer.aflibrary.extensions

import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import br.com.afischer.aflibrary.AFApp


fun Int.str(vararg params: Any): String {
        if (params.isEmpty()) {
                return AFApp().getString( this, "")
        }
        return AFApp().getString(this, *params)
}
fun Int.colorify(): Int = ResourcesCompat.getColor(AFApp().resources, this, null)



fun Int.attrColor(): Int {
        val typedValue = TypedValue()
        AFApp().theme.resolveAttribute(this, typedValue, true)
        val arr = AFApp().obtainStyledAttributes(typedValue.data, intArrayOf(this))
        val color = arr.getColor(0, -1)
        arr.recycle()
        return color
}


