package br.com.afischer.aflibrary.extensions

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


operator fun SharedPreferences.get(key: String, default: Boolean = false): Boolean = getBoolean(key, default)
operator fun SharedPreferences.get(key: String, default: String = ""): String = getString(key, default) ?: ""
operator fun SharedPreferences.get(key: String, default: Float = 0f): Float = getFloat(key, default)
operator fun SharedPreferences.get(key: String, default: Int = -1): Int = getInt(key, default)
operator fun SharedPreferences.get(key: String, default: Long = -1L): Long = getLong(key, default)
operator fun SharedPreferences.get(key: String, default: MutableSet<String> = mutableSetOf()): MutableSet<String> = getStringSet(key, default) ?: mutableSetOf()

operator fun SharedPreferences.set(key: String, default: Boolean) {
        edit().putBoolean(key, default).apply()
}
operator fun SharedPreferences.set(key: String, default: String) {
        edit().putString(key, default).apply()
}
operator fun SharedPreferences.set(key: String, default: Float) {
        edit().putFloat(key, default).apply()
}
operator fun SharedPreferences.set(key: String, default: Int) {
        edit().putInt(key, default).apply()
}
operator fun SharedPreferences.set(key: String, default: Long) {
        edit().putLong(key, default).apply()
}
operator fun SharedPreferences.set(key: String, default: MutableSet<String>) {
        edit().putStringSet(key, default).apply()
}

fun SharedPreferences.clear() {
        edit().clear().apply()
}



private inline fun <T> SharedPreferences.delegate(
        defaultValue: T,
        key: String?,
        crossinline getter: SharedPreferences.(String, T) -> T,
        crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor,
): ReadWriteProperty<Any, T> {
        return object : ReadWriteProperty<Any, T> {
                override fun getValue(thisRef: Any, property: KProperty<*>) = getter(key ?: property.name, defaultValue)
                override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = edit().setter(key ?: property.name, value).apply()
        }
}

fun SharedPreferences.int(def: Int = 0, key: String? = null) =
        delegate(def, key, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

fun SharedPreferences.long(def: Long = 0, key: String? = null) =
        delegate(def, key, SharedPreferences::getLong, SharedPreferences.Editor::putLong)

fun SharedPreferences.bool(def: Boolean = false, key: String? = null) =
        delegate(def, key, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

fun SharedPreferences.string(def: String = "", key: String? = null) =
        delegate(def, key, SharedPreferences::getString, SharedPreferences.Editor::putString)

fun SharedPreferences.float(def: Float = 0f, key: String? = null) =
        delegate(def, key, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)
