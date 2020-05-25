package com.example.bcon.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun View.setVisibleIf(boolean: () -> Boolean) {
    visibility = if (boolean()) View.VISIBLE else View.GONE
}

fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Fragment.showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
    context?.showToast(text, length)
}
