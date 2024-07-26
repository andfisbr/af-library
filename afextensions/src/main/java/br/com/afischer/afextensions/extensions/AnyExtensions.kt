package br.com.afischer.afextensions.extensions

import android.graphics.Bitmap
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


fun Any.toJson(): String = Gson().toJson(this)
inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)!!
fun Any.toPrettyJson(): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(this)

}



val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        isLenient = true
}

@ExperimentalSerializationApi
inline fun <reified T> String.objectfy(): T? {
        this.ifEmpty { return null }

        return try {
                json.decodeFromString<T>(this)
        } catch (ex: Exception) {
                null
        }
}

@ExperimentalSerializationApi
inline fun <reified T> T?.stringfy(): String {
        this ?: return ""

        return try {
                json.encodeToString(this)
        } catch (ex: Exception) {
                ""
        }
}




/**
 * json extensions
 */
fun JSONObject.toMap(): MutableMap<String, Any?>? {
        var retMap = mutableMapOf<String, Any?>()

        if (this !== JSONObject.NULL) {
                retMap = this.map()
        }

        return retMap
}

private fun JSONObject.map(): MutableMap<String, Any?> {
        val map = mutableMapOf<String, Any?>()

        val keysItr = this.keys()
        while(keysItr.hasNext()) {
                val key = keysItr.next()
                var value: Any? = null

                try {
                        value = this.get(key)
                } catch(ignored: JSONException) {
                }

                if (value is JSONArray) {
                        value = value.list()
                } else if(value is JSONObject) {
                        value = value.map()
                }

                map[key] = value
        }

        return map
}

private fun JSONArray.list(): MutableList<Any?> {
        val list = ArrayList<Any?>()

        (0 until this.length()).forEach {
                var value: Any? = null

                try {
                        value = this.get(it)
                } catch(ignored: JSONException) {
                }

                if (value is JSONArray) {
                        value = value.list()
                } else if(value is JSONObject) {
                        value = value.map()
                }

                list.add(value)
        }

        return list
}




fun Bitmap.dominantColor(): Int {
        val newBitmap = Bitmap.createScaledBitmap(this, 1, 1, true)
        val color = newBitmap.getPixel(0, 0)
        newBitmap.recycle()
        return color
}



fun Boolean.no(): Boolean = !this



fun TextView.linkfy(links: MutableList<String>, clickableSpans: MutableList<ClickableSpan>) {
        val spannableString = SpannableString(this.text)

        links.forEachIndexed { i, s ->
                val clickableSpan = clickableSpans[i]

                val startIndexOfLink = this.text.toString().indexOf(s)
                if (startIndexOfLink < 0) {
                        return@forEachIndexed
                }
                spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        this.highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
        this.movementMethod = LinkMovementMethod.getInstance()
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

