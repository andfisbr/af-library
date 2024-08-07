package br.com.afischer.aflibrary.extensions

import kotlinx.coroutines.Deferred


/**
* return T or null if exception was throw
*/
suspend fun <T> Deferred<T>.awaitSafe(): T? = try {
        await()
} catch (e: Exception) {
        e.printStackTrace()
        null
}
