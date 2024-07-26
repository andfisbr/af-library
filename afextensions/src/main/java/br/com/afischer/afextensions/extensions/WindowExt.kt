package br.com.afischer.afextensions.extensions

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat







fun Window.hideNavigationBar() {
        WindowInsetsControllerCompat(this, this.decorView).hide(WindowInsetsCompat.Type.navigationBars())
}


fun Window.hideStatusBar() {
        WindowInsetsControllerCompat(this, this.decorView).hide(WindowInsetsCompat.Type.statusBars())
}


fun Window.hideSystemBars() {
        WindowInsetsControllerCompat(this, this.decorView).hide(WindowInsetsCompat.Type.systemBars())
}


operator fun Window.minusAssign(flags: Int) {
        clearFlags(flags)
}


operator fun Window.plusAssign(flags: Int) {
        addFlags(flags)
}


fun Window.setBackgroundColor(@ColorInt color: Int) {
        setBackgroundDrawable(ColorDrawable(color))
}


fun Window.setCutoutMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
}


fun Window.setFitsSystemWindow(fits: Boolean = true) {
        WindowCompat.setDecorFitsSystemWindows(this, !fits)
}


fun Window.setFullScreen(showSystemBars: Boolean = false, systemBarsColor: Int = Color.TRANSPARENT) {
        setCutoutMode()
        if (!showSystemBars) {
                hideSystemBars()
        }
        setSystemBarsColor(systemBarsColor)
        setFitsSystemWindow()
}


fun Window.setNavigationBarColor(@ColorInt color: Int = Color.TRANSPARENT) {
        navigationBarColor = color
}


fun Window.setNavigationBarDark(isDark: Boolean = true) {
        WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightNavigationBars = isDark
}


fun Window.setStatusBarColor(@ColorInt color: Int = Color.TRANSPARENT) {
        statusBarColor = color
}


fun Window.setStatusBarDark(isDark: Boolean = true) {
        WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = isDark
}


fun Window.setSystemBarsBehavior(behavior: Int = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE) {
        WindowInsetsControllerCompat(this, this.decorView).systemBarsBehavior = behavior
}


fun Window.setSystemBarsColor(@ColorInt color: Int = Color.TRANSPARENT) {
        statusBarColor = color
        navigationBarColor = color
}


fun Window.setSystemBarsDark(isDark: Boolean = true) {
        WindowInsetsControllerCompat(this, this.decorView).apply {
                isAppearanceLightStatusBars = isDark
                isAppearanceLightNavigationBars = isDark
        }
}


fun Window.showNavigationBar() {
        WindowInsetsControllerCompat(this, this.decorView).show(WindowInsetsCompat.Type.navigationBars())
}


fun Window.showStatusBar() {
        WindowInsetsControllerCompat(this, this.decorView).show(WindowInsetsCompat.Type.statusBars())
}


fun Window.showSystemBars() {
        WindowInsetsControllerCompat(this, this.decorView).show(WindowInsetsCompat.Type.systemBars())
}

