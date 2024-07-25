package br.com.afischer.afextensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText




fun EditText.text(default: String = "") = if (this.text.toString() == "") {
        default
} else {
        this.text.toString()
}


fun EditText.doOnAction(action: Int, block: () -> Unit) {
        this.setOnEditorActionListener { view, actionId, keyEvent ->
                return@setOnEditorActionListener when (actionId) {
                        action -> {
                                block.invoke()
                                true
                        }
                        else -> false
                }
        }
}


inline fun EditText.onTextChanged(crossinline listener: (text: CharSequence?, start: Int, before: Int, count: Int) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        listener(s, start, before, count)
                }
        })
}

inline fun EditText.beforeTextChanged(crossinline listener: (text: CharSequence?, start: Int, count: Int, after: Int) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        listener(s, start, count, after)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
}

inline fun EditText.afterTextChanged(crossinline listener: (text: Editable?) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                        listener(s)
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
}
