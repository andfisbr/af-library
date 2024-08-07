package br.com.afischer.aflibrary.extensions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

fun Map<String, Any?>.stringfy(root: JSONObject = JSONObject()): String {
        for ((key, value) in this) {
                val parts = key.split("/").filter { it.isNotBlank() }

                if (parts.size == 1) {
                        root.put(key, value)
                } else {
                        var curr = root
                        parts.dropLast(1).forEach { section ->
                                if (!curr.has(section)) {
                                        curr.put(section, JSONObject())
                                }
                                curr = curr.getJSONObject(section)
                        }
                        val lastKey = parts.last()
                        curr.put(lastKey, value)
                }
        }

        return root.toString()
}

fun Array<Any?>.stringfy(): String {
        val sb = StringBuilder()
        sb.append("[")

        var first = true

        for (item in this) {
                if (!first) {
                        sb.append(",")
                }
                first = false

                sb.append(typeCheck(item))
        }

        sb.append("]")
        return sb.toString()
}

fun Collection<Any?>.stringfy(): String {
        val sb = StringBuilder()
        sb.append("[")

        var first = true

        for (item in this) {
                if (!first) {
                        sb.append(",")
                }
                first = false

                sb.append(typeCheck(item))
        }

        sb.append("]")
        return sb.toString()
}


fun JSONObject.toMap(): MutableMap<String, Any?> {
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





fun String.isJSON(): Boolean {
        try {
                JSONObject(this)
                
        } catch (ex1: JSONException) {
                try {
                        JSONArray(this)
                } catch (ex2: JSONException) {
                        return false
                }
        }
        return true
}
