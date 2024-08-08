package br.com.afischer.aflibrary.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.DrawableCompat
import br.com.afischer.aflibrary.AFLibraryApp
import br.com.afischer.aflibrary.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import jp.wasabeef.blurry.Blurry


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








inline fun View.waitForLayout(crossinline listener: () -> Unit) {
        val vto = viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                        when {
                                vto.isAlive -> {
                                        vto.removeOnGlobalLayoutListener(this)
                                        listener()
                                }
                                else -> viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                }
        })
}


inline fun <T: View> T.afterMeasured(crossinline f: T.() -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                        if (measuredWidth > 0 && measuredHeight > 0) {
                                viewTreeObserver.removeOnGlobalLayoutListener(this)
                                f()
                        }
                }
        })
}



fun ImageView.loadUrl(uri: Uri, placeholder: Int = R.drawable.ic_question, onSuccessListener: () -> Unit = {}, onErrorListener: () -> Unit = {}): ImageView {
        try {
                Picasso.get()
                        .load(uri)
                        .placeholder(placeholder)
                        .into(this, object: Callback {
                                override fun onSuccess() {
                                        onSuccessListener()
                                }
                                override fun onError(e: java.lang.Exception?) {
                                        setImageResource(placeholder)
                                        onErrorListener()
                                }
                        })

        } catch (ex: Exception) {
                setImageResource(placeholder)
                onErrorListener()
        }

        return this
}

fun ImageView.loadUrl(uri: String, placeholder: Int = R.drawable.ic_question, onSuccessListener: () -> Unit = {}, onErrorListener: () -> Unit = {}): ImageView {
        return this.loadUrl(Uri.parse(uri), placeholder, onSuccessListener, onErrorListener)
}


fun ImageView.blur(from: Drawable?): ImageView {
        if (from == null) {
                return this
        }

        val bitmap = if (from is VectorDrawable) {
                from.getBitmap()
        } else {
                (from as BitmapDrawable).bitmap
        }

        Blurry.with(AFLibraryApp())
                .from(bitmap)
                .into(this)

        return this
}
fun ImageView.blur(from: ImageView): ImageView {
        return this.blur(from.drawable)
}


fun VectorDrawable.getBitmap(): Bitmap {
        val drawable = DrawableCompat.wrap(this).mutate()

        val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
}
