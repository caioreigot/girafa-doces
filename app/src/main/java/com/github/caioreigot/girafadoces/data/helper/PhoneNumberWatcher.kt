package com.github.caioreigot.girafadoces.data.helper

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.github.caioreigot.girafadoces.data.helper.Utils.Companion.putCharSequenceBefore
import com.github.caioreigot.girafadoces.data.helper.Utils.Companion.removeLastWord

class PhoneNumberWatcher(private val et: EditText) : TextWatcher {

    private var ignore: Boolean = false

    private var textBeforeLength: Int = 0
    private var textCurrentLength: Int = 0
    private var erasing: Boolean = false

    override fun onTextChanged(
        s: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {}

    override fun afterTextChanged(s: Editable?) {
        if (ignore) return

        s?.let { text ->
            textCurrentLength = text.length
            erasing = textCurrentLength < textBeforeLength

            if (!erasing) {
                ignore = true
                when (text.length) {
                    1 -> et.putCharSequenceBefore("(")
                    4 -> et.putCharSequenceBefore(") ")
                    11 -> et.putCharSequenceBefore("-")
                    16 -> et.removeLastWord()
                }
                ignore = false
            }

            et.setSelection(et.length())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        s?.let { text -> textBeforeLength = text.length }
    }
}