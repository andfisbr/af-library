package br.com.afischer.afextensions.extensions

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.PixelCopy
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import br.com.afischer.afextensions.R
import br.com.afischer.afextensions.enums.AlerterType
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.tapadoo.alerter.Alert
import com.tapadoo.alerter.Alerter


inline val Activity.displaySizePixels: Point
        get() {
                val display = this.windowManager.defaultDisplay
                return DisplayMetrics().apply {
                        display.getRealMetrics(this)
                }.let {
                        Point(it.widthPixels, it.heightPixels)
                }
        }

inline val Activity.getStatusBarHeight: Int
        get() {
                val rect = Rect()
                window.decorView.getWindowVisibleDisplayFrame(rect)
                return rect.top
        }

inline val Activity.isImmersiveModeEnabled
        get() = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == window.decorView.systemUiVisibility

inline val Activity.isInMultiWindow: Boolean
        get() = isInMultiWindowMode

inline var Activity.isLightNavigationBar
        get() = WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars
        set(enabled) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = enabled
        }


inline var Activity.isLightStatusBar
        get() = WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars
        set(enabled) {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = enabled
        }

inline val Activity.rootView: View
        get() = findViewById(android.R.id.content)

private val Activity.statusBarHeight: Int
        get() {
                val id = resources.getIdentifier("status_bar_height", "dimen", "android")
                return resources.getDimensionPixelSize(id)
        }











fun Activity.alerter(type: AlerterType = AlerterType.NORMAL, duration: Long = 3000L, body: Alerter.() -> Unit): Alert? {
        return Alerter.create(this).apply {
                when (type) {
                        AlerterType.EXCEPTION -> {
                                setTitle("EXCEPTION")
                                setBackgroundColorRes(R.color.red_900)
                        }

                        AlerterType.WARNING -> {
                                setTitle("WARNING")
                                setBackgroundColorRes(R.color.orange_500)
                        }

                        else -> setBackgroundColorRes(R.color.teal_500)
                }
                setDismissable(true)
                enableIconPulse(true)
                enableSwipeToDismiss()
                //setLayoutGravity(Gravity.BOTTOM)
                setDuration(duration)

                body()
        }.show()
}


/**
 * Sets the screen brightness. Call this before setContentView.
 * 0 is dimmest, 1 is brightest. Default value is 1
 */
fun Activity.brightness(brightness: Float = 1f) {
        val params = window.attributes
        params.screenBrightness = brightness // range from 0 - 1 as per docs
        window.attributes = params
        window.addFlags(WindowManager.LayoutParams.FLAGS_CHANGED)
}


fun Activity.enableFullScreen() {
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
}


fun Activity.enterFullScreenMode() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}


fun Activity.exitFullScreenMode() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
}


fun <T> Activity.extra(key: String): Lazy<T?> {
        return lazy(LazyThreadSafetyMode.NONE) {
                @Suppress("UNCHECKED_CAST")
                intent.extras?.get(key) as T
        }
}


fun <T> Activity.extraOrNull(key: String): Lazy<T?> {
        return lazy(LazyThreadSafetyMode.NONE) {
                @Suppress("UNCHECKED_CAST")
                intent.extras?.get(key) as? T?
        }
}


fun Activity.fixSoftInputLeaks() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        val leakViews = arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")

        for (leakView in leakViews) {
                try {
                        val leakViewField = InputMethodManager::class.java.getDeclaredField(leakView) ?: continue
                        if (!leakViewField.isAccessible) {
                                leakViewField.isAccessible = true
                        }

                        val obj = leakViewField.get(imm) as? View ?: continue
                        if (obj.rootView === this.window.decorView.rootView) {
                                leakViewField.set(imm, null)
                        }

                } catch (ignore: Throwable) {
                        /**/
                }
        }
}


fun Activity.getBitmapFromUri(uri: Uri): Bitmap? {
        return contentResolver.openInputStream(uri)?.use {
                return@use BitmapFactory.decodeStream(it)
        }
}


fun AppCompatActivity.getDisplayDensity(): String {
        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)
        return when (metrics.densityDpi) {
                DisplayMetrics.DENSITY_LOW -> "LDPI"
                DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
                DisplayMetrics.DENSITY_HIGH -> "HDPI"
                DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
                DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
                DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
                else -> "XXHDPI"
        }
}


fun Activity.hideBottomBar() {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
}


fun Activity.hideNavigationBar() {
        WindowInsetsControllerCompat(this.window, this.window.decorView).hide(WindowInsetsCompat.Type.navigationBars())
}


fun Activity.hideSoftKeyboard(view: View? = null) {
        if (view != null && !view.isFocused) {
                return
        }

        val aux = view ?: (currentFocus ?: View(this))

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(aux.applicationWindowToken, 0)
}


fun Activity.hideStatusBar() {
        WindowInsetsControllerCompat(this.window, this.window.decorView).hide(WindowInsetsCompat.Type.statusBars())
}


fun Activity.hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
}


fun Activity.hideSystemBars() {
        WindowInsetsControllerCompat(this.window, this.window.decorView).hide(WindowInsetsCompat.Type.systemBars())
}


fun Activity.isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Int.MAX_VALUE).any { it.service.className == serviceClass.name }
}


fun Activity.keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}


fun Activity.keepScreenOff() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}


inline fun <reified T : Any> Activity.launchActivity(requestCode: Int = -1, options: Bundle? = null, noinline init: Intent.() -> Unit = {}) {
        val intent = this.newIntent<T>()
        intent.init()

        startActivityForResult(intent, requestCode, options)
}


inline fun <reified T : Any> Activity.launchActivityAndFinish() {
        val intent = this.newIntent<T>()
        startActivity(intent)
        finish()
}


fun Activity.lockOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
}


fun Activity.lockCurrentScreenOrientation() {
        requestedOrientation = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
}


fun Activity.makeSceneTransitionAnimation(view: View, transitionName: String): ActivityOptionsCompat =
        ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, transitionName)


fun AppCompatActivity.onSupportNavigateUpGoBack(): Boolean {
        onBackPressed()
        return true
}


fun Activity.openSettings() {
        val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", this.packageName, null)
        i.data = uri
        startActivityForResult(i, 101)
}


/**
 * An extension to `postponeEnterTransition` which will resume after a timeout.
 */
fun Activity.postponeEnterTransition(timeout: Long) {
        postponeEnterTransition()
        window.decorView.postDelayed({ startPostponedEnterTransition() }, timeout)
}


/**
 * Iterate through fragments attached to FragmentManager and pop's one after the other.
 */
fun AppCompatActivity.popWholeBackStack() {
        val fragmentManager = this.supportFragmentManager
        for (i in 0 until fragmentManager.fragments.size) {
                fragmentManager.popBackStack()
        }
}


fun Activity.requestPermissions(permissions: List<String>, listener: (result: Boolean, permanentlyDenied: Boolean) -> Unit) {
        Dexter.withContext(this)
                .withPermissions(permissions)
                .withListener(object: MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                // check if all permissions are granted
                                if (report!!.areAllPermissionsGranted()) {
                                        listener(true, false)
                                }

                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied) {
                                        listener(false, true)
                                }
                        }

                        override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                                token?.continuePermissionRequest()
                        }
                })
                .onSameThread()
                .check()
}



/**
 * Restarts an activity from itself with a fade animation
 * Keeps its existing extra bundles and has a intentBuilder to accept other parameters
 */
inline fun Activity.restartActivity(intentBuilder: Intent.() -> Unit = {}) {
        val i = Intent(this, this::class.java)
        val oldExtras = intent.extras
        if (oldExtras != null) {
                i.putExtras(oldExtras)
        }
        i.intentBuilder()
        startActivity(i)
        finish()
}


fun Activity.restartApplication() {
        try {
                val intent = packageManager.getLaunchIntentForPackage(applicationContext.packageName)!!
                val intentMain = Intent.makeRestartActivityTask(intent.component)
                applicationContext.startActivity(intentMain)
                Runtime.getRuntime().exit(0)

        } catch (_: Exception) {
                /**/
        }
}


fun Activity.selectAppTheme(basedOnUserChoice: Boolean = false, isDark: Boolean = false) {
        val isSystemDark = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        when {
                basedOnUserChoice -> if (isDark) {
                        setTheme(R.style.DarkTheme)
                } else {
                        setTheme(R.style.LightTheme)
                }

                (isSystemDark || isDark) -> setTheme(R.style.DarkTheme)

                else -> setTheme(R.style.LightTheme)
        }
}


fun Activity.selectScreenOrientation(isPortrait: Boolean = true) {
        requestedOrientation = if (isPortrait) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
}


fun Activity.setBackgroundColor(@ColorInt color: Int) {
        this.window.setBackgroundDrawable(ColorDrawable(color))
}


fun Activity.setCutoutMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                this.window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
}


fun Activity.setFitsSystemWindow(fits: Boolean = true) {
        WindowCompat.setDecorFitsSystemWindows(this.window, !fits)
}


fun Activity.setFullScreen(showSystemBars: Boolean = false, systemBarsColor: Int = Color.TRANSPARENT) {
        setCutoutMode()
        if (!showSystemBars) {
                hideSystemBars()
        }
        this.window.setSystemBarsColor(systemBarsColor)
        setFitsSystemWindow()
}


fun Activity.setNavigationBarColor(@ColorInt color: Int = Color.TRANSPARENT) {
        this.window.navigationBarColor = color
}


fun Activity.setNavigationBarDark(isDark: Boolean = true) {
        WindowInsetsControllerCompat(this.window, this.window.decorView).isAppearanceLightNavigationBars = isDark
}


@RequiresApi(Build.VERSION_CODES.P)
fun Activity.setNavigationBarDividerColor(@ColorInt color: Int) {
        window.navigationBarDividerColor = color
}


fun Activity.setStatusBarColor(@ColorInt color: Int = Color.TRANSPARENT) {
        this.window.statusBarColor = color
}


fun Activity.setStatusBarDark(isDark: Boolean = true) {
        WindowInsetsControllerCompat(this.window, this.window.decorView).isAppearanceLightStatusBars = isDark
}


fun Activity.setSystemBarsBehavior(behavior: Int = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE) {
        WindowInsetsControllerCompat(this.window, this.window.decorView).systemBarsBehavior = behavior
}


fun Activity.setSystemBarsColor(@ColorInt color: Int = Color.TRANSPARENT) {
        this.window.statusBarColor = color
        this.window.navigationBarColor = color
}


fun Activity.setSystemBarsDark(isDark: Boolean = true) {
        WindowInsetsControllerCompat(this.window, this.window.decorView).apply {
                isAppearanceLightStatusBars = isDark
                isAppearanceLightNavigationBars = isDark
        }
}


/**
 * Set the action bar title
 * @param title String res of the title
 */
fun AppCompatActivity.setToolbarTitle(title: String? = null, @StringRes resId: Int = 0) {
        val aux = when {
                title != null -> title
                resId != 0 -> this.baseContext.getString(resId)
                else -> throw IllegalArgumentException("title or resId must not be empty")
        }
        supportActionBar?.setTitle(aux)
}


fun Activity.setTransparentStatusBarFlags() {
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
}


fun AppCompatActivity.setupToolbar(toolbar: Toolbar, displayHomeAsUpEnabled: Boolean = true, displayShowHomeEnabled: Boolean = true, displayShowTitleEnabled: Boolean = false, showUpArrowAsCloseIcon: Boolean = false, @DrawableRes closeIconDrawableRes: Int? = null) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)
                setDisplayShowHomeEnabled(displayShowHomeEnabled)
                setDisplayShowTitleEnabled(displayShowTitleEnabled)

                if (showUpArrowAsCloseIcon && closeIconDrawableRes != null) {
                        setHomeAsUpIndicator(
                                AppCompatResources.getDrawable(
                                        this@setupToolbar,
                                        closeIconDrawableRes
                                )
                        )
                }
        }
}


fun Activity.setWindowFlag(bits: Int, on: Boolean) {
        val winParams = window.attributes
        if (on) {
                winParams.flags = winParams.flags or bits
        } else {
                winParams.flags = winParams.flags and bits.inv()
        }
        window.attributes = winParams
}


fun Activity.showBottomBar() {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_VISIBLE
        decorView.systemUiVisibility = uiOptions
}


fun Activity.showNavigationBar() {
        WindowInsetsControllerCompat(this.window, this.window.decorView).show(WindowInsetsCompat.Type.navigationBars())
}


fun Activity.showSoftKeyboard(toFocus: View) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        //imm.showSoftInput(toFocus.rootView, InputMethodManager.SHOW_IMPLICIT)
        toFocus.requestFocus()
}


fun Activity.showStatusBar() {
        WindowInsetsControllerCompat(this.window, this.window.decorView).show(WindowInsetsCompat.Type.statusBars())
}


fun Activity.showSystemBars() {
        WindowInsetsControllerCompat(this.window, this.window.decorView).show(WindowInsetsCompat.Type.systemBars())
}


fun Activity.showSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
}


inline fun <reified T> Activity.startActivityForResult(requestCode: Int, bundleBuilder: Bundle.() -> Unit = {}, intentBuilder: Intent.() -> Unit = {}) {
        val intent = Intent(this, T::class.java)
        intent.intentBuilder()
        val bundle = Bundle()
        bundle.bundleBuilder()
        startActivityForResult(intent, requestCode, if (bundle.isEmpty) null else bundle)
}


fun Activity.takeScreenShot(removeStatusBar: Boolean = false): Bitmap? {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val bmp = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.RGB_565)
        val canvas = Canvas(bmp)
        window.decorView.draw(canvas)


        return if (removeStatusBar) {
                val statusBarHeight = statusBarHeight
                Bitmap.createBitmap(
                        bmp,
                        0,
                        statusBarHeight,
                        dm.widthPixels,
                        dm.heightPixels - statusBarHeight
                )
        } else {
                Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
        }
}


fun Activity.takeScreenShot(removeStatusBar: Boolean = false, listener: (Int, Bitmap) -> Unit) {
        val rect = Rect()
        windowManager.defaultDisplay.getRectSize(rect)

        if (removeStatusBar) {
                val statusBarHeight = this.statusBarHeight

                rect.set(rect.left, rect.top + statusBarHeight, rect.right, rect.bottom)
        }
        val bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888)

        PixelCopy.request(this.window, rect, bitmap, {
                listener(it, bitmap)
        }, Handler(this.mainLooper))
}


fun Activity.unlockScreenOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
}