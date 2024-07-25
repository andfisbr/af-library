package br.com.afischer.afextensions


import android.text.*
import android.util.Base64
import br.com.afischer.afextensions.application.App
import br.com.afischer.afextensions.utils.Consts
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.Deflater
import java.util.zip.Inflater


fun String.openUrl(): String {
        val url = URL(this)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        try {
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val response = java.lang.StringBuilder()
                        var line: String?

                        while (reader.readLine().also { line = it } != null) {
                                response.append(line)
                        }
                        reader.close()

                        return response.toString()

                } else {
                        return "HTTP request failed. Response code: $responseCode"
                }

        } finally {
                connection.disconnect()
        }
}


/**
 * String extensions
 */
fun String.asDate(format: String = Consts.DATE_FORMAT): Date = if (format != Consts.DATE_FORMAT || this.matches("(?is)\\d{2}/(\\d{2})/\\d{4}".toRegex()))
        SimpleDateFormat(format, Consts.ptBR).parse(this)!!
else
        Date(this)



fun String.asDateLong(format: String = Consts.DATE_FORMAT): Long = SimpleDateFormat(format, Consts.ptBR).parse(this)!!.time
fun String.captalise(): String {
        val exceptions = "da das de do dos pda"
        var result = this.tlc()
        
        if ("" != result) {
                val words = result.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (index in words.indices) {
                        val s = words[index]
                        if (!exceptions.contains(s)) {
                                if (s.isNotEmpty()) {
                                        words[index] = s.substring(0, 1).tuc() + s.substring(1)
                                }
                        }
                }
                result = TextUtils.join(" ", words)
        }
        return result
}


fun String.asHtml(): Spanned {
        return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
}

fun String.encoded(): String {
        return URLEncoder.encode(this, "UTF-8")
}



fun String.md5(): String {
        try {
                // Create MD5 Hash
                val digest = java.security.MessageDigest.getInstance("MD5")
                digest.update(this.toByteArray())
                val messageDigest = digest.digest()
                
                // Create Hex String
                val hexString = StringBuffer()
                for (i in messageDigest.indices) {
                        hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
                }
                return hexString.toString()
                
        } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
        }
        
        return ""
}



fun String.normalise(): String = Normalizer.normalize(this, Normalizer.Form.NFD).replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")


fun String.tlc(): String = this.lowercase(Locale.getDefault())
fun String.tuc(): String = this.uppercase(Locale.getDefault())


fun String.toRange(): IntRange {
        val ini = this.split("..")[0].toInt()
        val fim = this.split("..")[1].toInt()
        return IntRange(ini, fim)
}






/**
 * Calculates the similarity (a number within 0 and 1) between two strings.
 */
fun String.similarity(other: String): Double {
        var longer = this
        var shorter = other
        
        
        //
        // longer should always have greater length
        //
        if (this.length < other.length) {
                longer = other
                shorter = this
        }
        
        
        val longerLength = longer.length
        
        //
        // both strings are zero length
        //
        if (longerLength == 0) {
                return 1.0
        }
        
        /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
        
        
        
        val s1 = longer.lowercase(Locale.getDefault())
        val s2 = shorter.lowercase(Locale.getDefault())
        val costs = IntArray(s2.length + 1)
        
        for (i in 0..s1.length) {
                var lastValue = i
                
                for (j in 0..s2.length) {
                        if (i == 0) {
                                costs[j] = j
                                
                        } else if (j > 0) {
                                var newValue = costs[j - 1]
                                if (s1[i - 1] != s2[j - 1]) {
                                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1
                                }
                                costs[j - 1] = lastValue
                                lastValue = newValue
                        }
                }
                
                if (i > 0) {
                        costs[s2.length] = lastValue
                }
        }
        
        return (longerLength - costs[s2.length]) / longerLength.toDouble()
}



fun String.pad(format: String = "<0"): String {
        val length = format.length - 1
        val char = format[1]
        val direction = format[0]
        
        return when (direction) {
                '>' -> this.padEnd(length, char)
                '<' -> this.padStart(length, char)
                else -> this.padStart(length, char)
        }
}


fun String.charTo(pos: Int, char: Char): String {
        val chars = this.toCharArray()
        chars[pos] = char
        return chars.joinToString("", "")
}

fun String.insert(char: Char, pos: Int): String {
        val p = if (pos < 0) {
                this.length + pos
        } else {
                pos
        }
        val sb = StringBuilder(this)
        sb.insert(p, char)
        return sb.toString()
        
        
}



fun String.weekF(): String {
        val days = arrayOf("dom", "seg", "ter", "qua", "qui", "sex", "sáb")
        var aux = ""
        
        
        this.forEachIndexed { i, c ->
                aux += if (c == '1') "<strong>${days[i]}</strong>" else days[i]
                if (i < this.length - 1)
                        aux += " "
        }
        
        return aux
}



fun String.obfuscate(): String {
        val chars = this.toCharArray()
        val sb = mutableListOf<String>()
        
        chars.forEach { n ->
                val r = (1..1000).random()
                val k = (1..4).random()
                
                sb.add(when (k) {
                        1 -> "${"%04x".format(r + n.code)}-$r"
                        2 -> "${n.code - r}+%04x".format(r)
                        else -> "${n.code * r}/%04x".format(r)
                })
        }
        
        return sb.joinToString("AF75")
}



fun String.deobfuscate(): String {
        val hexs = this.split("AF75")
        val sb = mutableListOf<String>()
        
        hexs.forEach {
                when {
                        it.contains("+") -> {
                                val v = it.split("+")
                                sb.add((v[0].toInt() + v[1].toInt(16)).toChar().toString())
                        }
                        
                        it.contains("-") -> {
                                val v = it.split("-")
                                sb.add((v[0].toInt(16) - v[1].toInt()).toChar().toString())
                        }
                        
                        else -> {
                                val v = it.split("/")
                                sb.add((v[0].toInt() / v[1].toInt(16)).toChar().toString())
                        }
                }
        }
        
        return sb.joinToString("")
}



fun String.leaf(delimiter: String = "."): String = this.split(delimiter).last()



fun String.toMillis(): Long {
        val parts = this.split(":")
        
        var hh = 0
        var mm = 0
        var ss = 0
        
        
        if (parts.size == 3) {
                hh = parts[0].toInt() * 3600000
                mm = parts[1].toInt() * 60000
                ss = parts[2].toInt() * 1000
                
        } else if (parts.size == 2) {
                hh = 0
                mm = parts[0].toInt() * 60000
                ss = parts[1].toInt() * 1000
        }
        
        
        return (hh + mm + ss).toLong()
}



fun String.nullToEmpty(): String = if (this == "null") {
        ""
} else {
        this
}









fun String.asSpannableString() = SpannableString(this)
fun CharSequence.asSpannableString() = this as? SpannableString ?: SpannableString("")

fun String.font(path: String = "", style: String): Spannable {
        return this.asSpannableString().font(path, style)
}

fun CharSequence.font(path: String = "", style: String): Spannable {
        return this.asSpannableString().font(path, style)
}




fun String.elze(elseOption: String): String {
        return if (this == "" || this == "[]" || this == "{}") {
                elseOption
        } else {
                this
        }
}


fun String.mid(count: Int): String {
        return if (this.length < count) {
                this
        } else {
                this.slice(0 until count)
        }
}



@JvmOverloads
fun String.isEmptyThenReturn(default: String = "") = if (TextUtils.isEmpty(this)) default else this





@JvmOverloads
fun String?.tryDouble(default: Double = 0.0): Double {
        if (this == null) {
                return default
        }

        return try {
                this.toDouble()
        } catch (ex: Exception) {
                default
        }
}






@JvmOverloads
fun String.tryInt(default: Int = 0): Int {
        return try {
                this.toInt()
        } catch (ex: Exception) {
                default
        }
}



fun String.group(): String {
        val dfs = DecimalFormatSymbols()
        val separator = dfs.decimalSeparator

        var aux = ""
        var found = false
        var count = -1

        this.forEachIndexed { index, char ->
                aux += char

                if (char == separator || found) {
                        found = true
                        count += 1
                }


                if (count % 3 == 0 && count > 0 && index < this.length - 1) {
                        aux += "{` ` <text-size:1px/>}"
                }
        }


        return aux
}




fun String.identifier(): Int = App().resources.getIdentifier(this, "drawable", App().packageName)





fun String.compress(): String? {
        val bytes: ByteArray = this.toByteArray(charset("UTF-8"))
        val deflater = Deflater(1, true)
        deflater.setInput(bytes)
        deflater.finish()

        val outputStream = ByteArrayOutputStream(bytes.size)

        try {
                val bytesCompressed = ByteArray(Short.MAX_VALUE.toInt())
                val numberOfBytesAfterCompression = deflater.deflate(bytesCompressed)
                val returnValues = ByteArray(numberOfBytesAfterCompression)
                System.arraycopy(bytesCompressed, 0, returnValues, 0, numberOfBytesAfterCompression)

                return Base64.encodeToString(returnValues, Base64.DEFAULT)

        } catch (e: IOException) {
                e.printStackTrace()

        } finally {
                deflater.end()
                outputStream.close()
        }


        return null
}


fun String.decompress(): String? {
        try {
                val bytes: ByteArray = Base64.decode(this, Base64.DEFAULT)

                val inflater = Inflater(true)
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                inflater.setInput(bytes)

                while (!inflater.finished()) {
                        val count = inflater.inflate(buffer)
                        outputStream.write(buffer, 0, count)
                }
                inflater.end()
                outputStream.close()

                return outputStream.toString("UTF8")

        } catch (e: IOException) {
                e.printStackTrace()
        }

        return null
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