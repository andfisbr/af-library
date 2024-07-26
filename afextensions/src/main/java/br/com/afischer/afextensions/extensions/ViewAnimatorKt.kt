package br.com.afischer.afextensions.extensions

import android.view.*
import com.github.florent37.viewanimator.*



class ViewAnimatorKt(view: View) {
        val animate = ViewAnimator.animate(view)
        internal fun start() {
                animate.start()
        }
}
fun ViewAnimatorKt.alpha(vararg alpha: Float) {
        animate.alpha(*alpha)
}
fun ViewAnimatorKt.translationY(vararg y: Float) {
        animate.translationY(*y)
}
fun ViewAnimatorKt.translationX(vararg x: Float) {
        animate.translationX(*x)
}
fun ViewAnimatorKt.duration(duration: Long) {
        animate.duration(duration)
}
fun ViewAnimatorKt.onStart(listener: () -> Unit) {
        animate.onStart(listener)
}
fun ViewAnimatorKt.onStop(listener: () -> Unit) {
        animate.onStop(listener)
}
fun View.animate(setup: ViewAnimatorKt.() -> Unit) {
        val starter = ViewAnimatorKt(this)
        starter.setup()
        starter.start()
}
