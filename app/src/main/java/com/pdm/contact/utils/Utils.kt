package com.pdm.contact.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.pdm.contact.feature.domain.model.Contact

object Utils {

    const val RESULT_CONTACT_EDIT = 123
    const val RESULT_CONTACT_ADD = 1234

    const val RV_INSTANCE = "rv_instance"

    var Intent?.contact: Contact?
        get() = this?.getSerializableExtra("contact") as? Contact
        set(value) {
            this?.putExtra("contact", value)
        }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun RecyclerView.getParcelable(savedInstanceState: Bundle?, key: String) {
        layoutManager?.onRestoreInstanceState(savedInstanceState?.getParcelable(key))
    }

    fun RecyclerView.setParcelable(outState: Bundle?, key: String) {
        outState?.putParcelable(key, layoutManager?.onSaveInstanceState())
    }

    fun Activity.startToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}