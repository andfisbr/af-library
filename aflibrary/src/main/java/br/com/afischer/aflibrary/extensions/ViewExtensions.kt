package br.com.afischer.aflibrary.extensions

import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi


fun View.asRect(): Rect {
        val l = IntArray(2)

        this.getLocationOnScreen(l)
        val rect = Rect(l[0], l[1], l[0] + this.width, l[1] + this.height)

        return rect
}




val View.hasNotch: Boolean
        @RequiresApi(Build.VERSION_CODES.P)
        get() {
                return rootWindowInsets?.displayCutout != null
        }


fun View.hide(gone: Boolean = true): View {
        visibility = if (gone) { View.GONE } else { View.INVISIBLE }
        return this
}



fun View.hideIf(condition: Boolean, gone: Boolean = true) {
        if (condition) {
                this.hide(gone)
        } else {
                this.show()
        }
}




inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
        if (layoutParams is T) block(layoutParams as T)
}


fun View.margin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
        layoutParams<ViewGroup.MarginLayoutParams> {
                left?.run { leftMargin = this }
                top?.run { topMargin = this }
                right?.run { rightMargin = this }
                bottom?.run { bottomMargin = this }
        }
}


fun View.show(): View {
        visibility = View.VISIBLE
        return this
}


fun View.showIf(condition: Boolean = true, gone: Boolean = true) {
        if (condition) {
                this.show()
        } else {
                this.hide(gone)
        }
}


fun View.toggle(gone: Boolean = true) {
        if (this.isShown) {
                this.hide(gone)

        } else {
                this.show()
        }
}


fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(this.context).inflate(layoutRes, this, attachToRoot)

