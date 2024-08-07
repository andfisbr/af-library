package br.com.afischer.aflibrary.extensions

import android.view.View
import android.view.ViewGroup
import com.github.florent37.viewanimator.AnimationBuilder
import com.github.florent37.viewanimator.ViewAnimator


fun View.pulseAnimation(fromScale: Float = 1f, toScale: Float = .9f, duration: Long = 1500L): View =
        apply {
                animate()
                        .scaleX(toScale)
                        .scaleY(toScale)
                        .setDuration(duration)
                        .withEndAction { pulseAnimation(toScale, fromScale, duration) }
                        .start()
        }

fun View.alphaAnimation(fromAlpha: Float = 0f, toAlpha: Float = 1f, duration: Long = 1500L): View =
        apply {
                alpha = fromAlpha
                animate()
                        .alpha(toAlpha)
                        .setDuration(duration)
                        .start()
        }

fun View.tadaAnimation(repeat: Int = 0) {
        ViewAnimator.animate(this)
                .tada()
                .repeatCount(repeat)
                .repeatMode(ViewAnimator.RESTART)
                .start()
        
}

fun View.animateWithAlpha(start: Float, end: Float, duration: Long = 700) {
        ViewAnimator.animate(this)
                .alpha(start, end)
                .duration(duration)
                .start()
}


fun View.doAnimation(block: AnimationBuilder.() -> Unit) {
        ViewAnimator.animate(this).apply(block)
}

fun View.hideAnimated(duration: Long = 400, vararg y: Float = floatArrayOf(0f, 1000f)) {
        if (this.isShown) {
                ViewAnimator.animate(this)
                        .translationY(*y)
                        .alpha(1f, 0f)
                        .duration(duration)
                        .onStop { this.hide() }
                        .start()
        }
}

fun View.hideWithAnimation(duration: Long = 700) {
        if (this.isShown) {
                ViewAnimator.animate(this)
                        .alpha(1f, 0f)
                        .translationY(0f, 1000f)
                        .duration(duration)
                        .onStop { this.hide() }
                        .start()
        }
}

fun View.showAnimated(body: AnimationBuilder.() -> Unit) {
        ViewAnimator.animate(this).apply {
                body()
                onStart { this@showAnimated.show() }
                start()
        }
}


fun View.showAnimated(duration: Long = 400, vararg y: Float = floatArrayOf(1000f, 0f)) {
        ViewAnimator.animate(this)
                .translationY(*y)
                .alpha(0f, 1f)
                .duration(duration)
                .onStart { this.show() }
                .start()
}

fun View.showWithAnimation(duration: Long = 700) {
        if (!this.isShown) {
                ViewAnimator.animate(this)
                        .alpha(0f, 1f)
                        .translationY(1000f, 0f)
                        .duration(duration)
                        .onStart { this.show() }
                        .decelerate()
                        .start()
        }
}

fun ViewGroup.alphaAnimation(fromAlpha: Float = 0f, toAlpha: Float = 1f, duration: Long = 1500L): ViewGroup =
        apply {
                alpha = fromAlpha
                animate()
                        .alpha(toAlpha)
                        .setDuration(duration)
                        .start()
        }

fun ViewGroup.doAnimation(block: AnimationBuilder.() -> Unit) {
        ViewAnimator.animate(this).apply(block)
}

