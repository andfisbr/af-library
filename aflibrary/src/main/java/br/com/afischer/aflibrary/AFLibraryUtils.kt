package br.com.afischer.aflibrary

object AFLibraryUtils {

        fun isAppInstalled(pkgName: String): Boolean {
                return try {
                        AFLibraryApp().packageManager.getApplicationInfo(pkgName, 0).enabled
                } catch (ex: Exception) {
                        false
                }
        }
}