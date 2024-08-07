package br.com.afischer.aflibrary.extensions


fun <T> Boolean.iif(ifOption: T, elseOption: T): T {
        return if (this) {
                ifOption
        } else {
                elseOption
        }
}
fun Boolean.no(): Boolean = !this


data class Ternary<T>(val target: T, val result: Boolean)
infix fun <T> Boolean.then(target: T): Ternary<T> {
        return Ternary(target, this)
}
infix fun <T> Ternary<T>.elze(target: T): T {
        return if (this.result) { this.target } else { target }
}