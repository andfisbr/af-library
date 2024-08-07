package br.com.afischer.aflibrary.extensions


import android.graphics.Color
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.ITALIC
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import java.util.regex.Pattern


fun Spannable.font(path: String = "", style: String): Spannable {
        
        var pattern = Pattern.compile("^$")
        var matcher = pattern.matcher(style)


        if (style.contains("bold")) {
                this.bold(path)
        }
        if (style.contains("italic")) {
                this.italic(path)
        }
        if (style.contains("underline")) {
                this.underline(path)
        }
        if (style.contains("strike")) {
                this.strike(path)
        }
        if (style.contains("sup")) {
                this.superscript(path)
        }
        if (style.contains("sub")) {
                this.subscript(path)
        }
        if (style.contains("[fb]#".toRegex())) {
                pattern = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})")
                matcher = pattern.matcher(style)
                if (matcher.find()) {
                        val color = Color.parseColor(matcher.group(0))
                        if (style.contains("f#")) {
                                this.color(color, path)
                        } else {
                                this.backgroundColor(color, path)
                        }
                }
        }
        if (style.contains("[fb]:".toRegex())) {
                pattern = Pattern.compile("[fb]:\\w+")
                matcher = pattern.matcher(style)
                if (matcher.find()) {
                        val c = matcher.group(0).replace("[fb]:".toRegex(), "")
                        val color = Color.parseColor(c)
                        if (style.contains("f:")) {
                                this.color(color, path)
                        } else {
                                this.backgroundColor(color, path)
                        }
                }
        }
        if (style.contains("\\d+(px)?".toRegex())) {
                pattern = Pattern.compile("\\d+(px)?")
                matcher = pattern.matcher(style)
                if (matcher.find()) {
                        val size = matcher.group(0)
                        this.fontSize(size, path)
                }
        }
        
        return this
}







private const val EMPTY_STRING = ""


fun spannable(func: () -> SpannableString) = func()

private fun span(s: CharSequence, o: Any, p: String = ""): SpannableString {
        val start = if (p.isNotEmpty()) { s.indexOf(p) } else { 0 }
        val until = if (p.isNotEmpty()) { start + p.length } else { s.length }
        
        return getNewSpannableString(s).apply {
                setSpan(o, start, until , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
}

private fun getNewSpannableString(charSequence: CharSequence): SpannableString {
        return if (charSequence is String) {
                SpannableString(charSequence)
        } else {
                charSequence as? SpannableString ?: SpannableString(EMPTY_STRING)
        }
}

operator fun SpannableString.plus(s: CharSequence) = SpannableString(TextUtils.concat(this, "", s))

fun CharSequence.makeSpannableString() = span(this, Spanned.SPAN_COMPOSING)
fun CharSequence.bold(path: String = "") = span(this, StyleSpan(BOLD), path)
fun CharSequence.italic(path: String = "") = span(this, StyleSpan(ITALIC), path)
fun CharSequence.underline(path: String = "") = span(this, UnderlineSpan(), path)
fun CharSequence.strike(path: String = "") = span(this, StrikethroughSpan(), path)
fun CharSequence.superscript(path: String = "") = span(this, SuperscriptSpan(), path)
fun CharSequence.subscript(path: String = "") = span(this, SubscriptSpan(), path)
fun CharSequence.fontSize(size: String, path: String = ""): SpannableString {
        val pattern = Pattern.compile("\\d+")
        val matcher = pattern.matcher(size)
        var s = 10
        if (matcher.find()) {
                s = matcher.group(0).toInt()
                if (size.contains("px")) {
                        s = s.dpToPx()
                }
        }
        return span(this, AbsoluteSizeSpan(s), path)
}
fun CharSequence.color(color: Int, path: String = "") = span(this, ForegroundColorSpan(color), path)
fun CharSequence.backgroundColor(color: Int, path: String = "") = span(this, BackgroundColorSpan(color), path)
fun CharSequence.url(url: String) = span(this, URLSpan(url))






fun String.spans(): SpannableStringBuilder {
        val styles = mutableMapOf(
                "<b>([^<]*)</b>" to Style(StyleSpan(BOLD), 4, 3),
                "<u>([^<]*)</u>" to Style(UnderlineSpan(), 4, 3),
                "<i>([^<]*)</i>" to Style(StyleSpan(ITALIC), 4, 3),
                "<fs6>([^<]*)</fs>" to Style(AbsoluteSizeSpan(6.dpToPx()), 5, 5),
                "<fs8>([^<]*)</fs>" to Style(AbsoluteSizeSpan(8.dpToPx()), 5, 5),
                "<fs10>([^<]*)</fs>" to Style(AbsoluteSizeSpan(10.dpToPx()), 5, 6),
                "<fs12>([^<]*)</fs>" to Style(AbsoluteSizeSpan(12.dpToPx()), 5, 6),
                "<fs14>([^<]*)</fs>" to Style(AbsoluteSizeSpan(14.dpToPx()), 5, 6),
                "<fs16>([^<]*)</fs>" to Style(AbsoluteSizeSpan(16.dpToPx()), 5, 6),
                "<fs18>([^<]*)</fs>" to Style(AbsoluteSizeSpan(16.dpToPx()), 5, 6)
        )

        
        
        val sb = SpannableStringBuilder()
        val aux = this.split("|")
        
        
        aux.forEach { item ->
                styles.keys.forEach { key ->
                        val p = Pattern.compile(key, Pattern.MULTILINE or Pattern.DOTALL or Pattern.CASE_INSENSITIVE)
                        var m = p.matcher(item)
                        if (m.find()) {
                                val s = styles[key]!!
        
                                val temp = SpannableStringBuilder(item)
                                temp.setSpan(s.span, m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        
                                temp.delete(m.end() - s.end, m.end())
                                temp.delete(m.start(), m.start() + s.start)
        
                                sb.append(temp)
                        }

                }
        }
        
        
        return sb
}



class Style(
        var span: Any = StyleSpan(android.graphics.Typeface.BOLD),
        var end: Int = 0,
        var start: Int = 0
)