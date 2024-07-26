package br.com.afischer.afextensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object AFExtUtils {

        fun isAppInstalled(pkgName: String): Boolean {
                return try {
                        AFExtApp().packageManager.getApplicationInfo(pkgName, 0).enabled
                } catch (ex: Exception) {
                        false
                }
        }
}