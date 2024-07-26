package br.com.afischer.aflibrary

object AFUtils {

        fun isAppInstalled(pkgName: String): Boolean {
                return try {
                        AFApp().packageManager.getApplicationInfo(pkgName, 0).enabled
                } catch (ex: Exception) {
                        false
                }
        }
}