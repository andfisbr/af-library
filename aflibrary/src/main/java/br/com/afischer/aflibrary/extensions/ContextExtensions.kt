package br.com.afischer.aflibrary.extensions

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import br.com.afischer.aflibrary.AFLibraryUtils
import br.com.afischer.aflibrary.R
import javax.crypto.Cipher


val Context.activityManager: ActivityManager
        get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

val Context.notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

inline var Context.sleepDuration: Int
        @RequiresPermission(Manifest.permission.WRITE_SETTINGS)
        set(value) {
                Settings.System.putInt(
                        this.contentResolver,
                        Settings.System.SCREEN_OFF_TIMEOUT,
                        value
                )
        }
        get() {
                return try {
                        Settings.System.getInt(
                                this.contentResolver,
                                Settings.System.SCREEN_OFF_TIMEOUT
                        )
                } catch (e: Settings.SettingNotFoundException) {
                        e.printStackTrace()
                        -123
                }
        }







fun Context.asActivity(): Activity = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext as Activity
        else -> throw IllegalStateException("Context $this NOT contains activity!")
}


fun Context.asFragmentActivity(): FragmentActivity = when (this) {
        is FragmentActivity -> this
        is Activity -> throw IllegalStateException("Context $this NOT supported Activity")
        is ContextWrapper -> baseContext as FragmentActivity
        else -> throw IllegalStateException("Context $this NOT contains activity!")
}


fun Context.browse(url: String, newTask: Boolean = false): Boolean {
        return try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                if (newTask) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
                true

        } catch (ex: ActivityNotFoundException) {
                ex.printStackTrace()
                false
        }
}


fun Context.callBiometricChecker(
        title: String,
        message: String? = null,
        negativeLabel: String,
        confirmationRequired: Boolean = true,
        initializedCipher: Cipher? = null,
        onAuthSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onAuthError: (Int, String) -> Unit
) {

        val executor = ContextCompat.getMainExecutor(this)

        val prompt = BiometricPrompt(
                this as FragmentActivity,
                executor,
                object: BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                onAuthSuccess(result)
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                onAuthError(errorCode, errString.toString())
                        }
                }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .apply { if (message != null) { setDescription(message) } }
                .setConfirmationRequired(confirmationRequired)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setNegativeButtonText(negativeLabel)
                .build()

        initializedCipher?.let {
                val cryptoObject = BiometricPrompt.CryptoObject(it)
                prompt.authenticate(promptInfo, cryptoObject)
                return
        }

        prompt.authenticate(promptInfo)
}


@JvmOverloads
fun Context.createNotificationChannel(id: String = "", name: String = "", description: String = "", importance: Int = NotificationManager.IMPORTANCE_HIGH, enableLights: Boolean = false, lightsColor: Int = Color.GREEN): String {
        val newId = id.isEmptyThenReturn(this.packageName)
        val appName = if (applicationInfo.labelRes != 0) getString(applicationInfo.labelRes) else applicationInfo.nonLocalizedLabel.toString()
        val newName = name.isEmptyThenReturn(appName)
        val newDescription = description.isEmptyThenReturn(appName)

        val channel = NotificationChannel(newId, newName, importance)
        channel.description = newDescription
        channel.enableLights(enableLights)
        channel.lightColor = lightsColor

        notificationManager.createNotificationChannel(channel)
        return newId
}


fun Context.hasPermission(permission: String): Boolean {
        val res = this.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
}


fun Context.hasPermissions(permissions: MutableList<String>): Boolean {
        var hasAllPermissions = true
        
        permissions.forEach {
                if (!hasPermission(it)) {
                        hasAllPermissions = false
                }
        }
        
        return hasAllPermissions
}


fun Context.isServiceRunning(serviceClass: Class<*>): Boolean =
        activityManager.getRunningServices(Integer.MAX_VALUE)!!.any { serviceClass.name == it.service.className }


inline fun <reified T : Any> Context.launchActivity(options: Bundle? = null, noinline init: Intent.() -> Unit = {}) {
        val intent = this.newIntent<T>()
        intent.init()
        startActivity(intent, options)
}


inline fun <reified T : Any> Context.newIntent(): Intent =
        Intent(this, T::class.java)



fun Context.share(message: String = "") {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, message)

        startActivity(Intent.createChooser(i, R.string.send_to.str()))
}


fun Context.telegram(id: String) {
        if (!AFLibraryUtils.isAppInstalled("org.telegram.messenger")) {
                Toast.makeText(this, R.string.telegram_not_installed, Toast.LENGTH_SHORT).show()
        
                try {
                        browse("market://details?id=org.telegram.messenger")
                } catch (anfe: android.content.ActivityNotFoundException) {
                        browse("https://play.google.com/store/apps/details?id=org.telegram.messenger")
                }
        
                return
        }
        
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$id"))
        startActivity(intent)
}


fun Context.whatsapp(phone: String = "", message: String = "") {
        if (!AFLibraryUtils.isAppInstalled("com.whatsapp")) {
                Toast.makeText(this, "WhatsApp não está instalado", Toast.LENGTH_SHORT).show()

                try {
                        browse("market://details?id=com.whatsapp")
                } catch (anfe: android.content.ActivityNotFoundException) {
                        browse("https://play.google.com/store/apps/details?id=com.whatsapp")
                }

                return
        }


        if (phone.isNotEmpty()) {
                browse("https://api.whatsapp.com/send?phone=$phone&text=${message.encoded()}")

        } else {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.setPackage("com.whatsapp")
                intent.putExtra(Intent.EXTRA_TEXT, message)
                startActivity(intent)
        }
}