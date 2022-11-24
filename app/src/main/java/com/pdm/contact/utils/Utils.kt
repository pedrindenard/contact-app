package com.pdm.contact.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.pdm.contact.R
import com.pdm.contact.feature.domain.model.Contact
import java.util.*

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

    fun ListPopupWindow.setBackgroundColor(@DrawableRes drawable: Int, context: Context) {
        setBackgroundDrawable(ContextCompat.getDrawable(context, drawable))
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(substringBefore(delimiter = " "))

    fun getCountryCodes(): List<String> {
        val arrayListOfCountry = arrayListOf<String>()

        for (countryCode in PhoneNumberUtil.getInstance().supportedRegions) {
            val phoneCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryCode)
            val displayCountry = Locale("", countryCode).displayCountry
            arrayListOfCountry.add("+$phoneCode $displayCountry")
        }

        return arrayListOfCountry
    }

    fun getCountryCode(): String {
        for (countryCode in PhoneNumberUtil.getInstance().supportedRegions) {
            if (countryCode == Locale.getDefault().country) {
                val phoneCode = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryCode)
                return "+$phoneCode"
            }
        }
        return "+55"
    }

    fun RecyclerView.addOnScrollHiddenView(hiddenView: View) {
        var isViewShown = true
        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    dy > 0 && isViewShown -> {
                        isViewShown = false
                        hiddenView.startEndAnimation()
                        hiddenView.visibility = View.GONE
                    }
                    dy < 0 && !isViewShown -> {
                        isViewShown = true
                        hiddenView.startOpenAnimation()
                        hiddenView.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun View.startOpenAnimation() {
        if (isGone) {
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade_in))
            visibility = View.VISIBLE
        }
    }

    fun View.startEndAnimation() {
        if (isVisible) {
            startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fade_out))
            visibility = View.GONE
        }
    }
}