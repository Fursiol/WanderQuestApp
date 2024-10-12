package com.example.wanderquest.utlis

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.inputmethod.InputMethodManager

object Helpers  {
    fun String.isEmailValid() : Boolean {
        return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

//    fun Activity.hideSoftKeyboard() {
//        currentFocus?.let {
//            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//            imm?.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
//        }
//    }

}