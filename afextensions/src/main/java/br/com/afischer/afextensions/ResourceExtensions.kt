package br.com.afischer.afextensions

import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import br.com.afischer.afextensions.application.App


fun Int.str(vararg params: Any): String {
        if (params.isEmpty()) {
                return App().getString( this, "")
        }
        return App().getString(this, *params)
}
fun Int.colorify(): Int = ResourcesCompat.getColor(App().resources, this, null)



fun Int.attrColor(): Int {
        val typedValue = TypedValue()
        App().theme.resolveAttribute(this, typedValue, true)
        val arr = App().obtainStyledAttributes(typedValue.data, intArrayOf(this))
        val color = arr.getColor(0, -1)
        arr.recycle()
        return color
}


