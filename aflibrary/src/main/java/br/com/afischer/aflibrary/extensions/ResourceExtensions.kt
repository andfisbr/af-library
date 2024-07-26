package br.com.afischer.aflibrary.extensions

import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import br.com.afischer.aflibrary.AFLibraryApp


fun Int.str(vararg params: Any): String {
        if (params.isEmpty()) {
                return AFLibraryApp().getString( this, "")
        }
        return AFLibraryApp().getString(this, *params)
}
fun Int.colorify(): Int = ResourcesCompat.getColor(AFLibraryApp().resources, this, null)



fun Int.attrColor(): Int {
        val typedValue = TypedValue()
        AFLibraryApp().theme.resolveAttribute(this, typedValue, true)
        val arr = AFLibraryApp().obtainStyledAttributes(typedValue.data, intArrayOf(this))
        val color = arr.getColor(0, -1)
        arr.recycle()
        return color
}


