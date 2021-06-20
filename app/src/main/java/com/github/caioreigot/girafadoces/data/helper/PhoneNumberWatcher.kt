package com.github.caioreigot.girafadoces.data.helper

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.github.caioreigot.girafadoces.data.helper.Utils.Companion.putCharBefore
import com.github.caioreigot.girafadoces.data.helper.Utils.Companion.removeLastWord

class PhoneNumberWatcher(private val et: EditText) : TextWatcher {

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
        s?.let { text ->
            textCurrentLength = text.length
            erasing = textCurrentLength < textBeforeLength

            if (!erasing) {
                when (text.length) {
                    1 -> et.putCharBefore("(")
                    4 -> et.putCharBefore(")")
                    5 -> et.putCharBefore(" ")
                    11 -> et.putCharBefore("-")
                    16 -> et.removeLastWord()
                }
            }

            et.setSelection(et.length())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        s?.let { text -> textBeforeLength = text.length }
    }
}