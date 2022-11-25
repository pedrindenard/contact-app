package com.pdm.contact.presentation.form

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pdm.contact.R
import com.pdm.contact.databinding.ActivityFormBinding
import com.pdm.contact.feature.domain.FormEvent
import com.pdm.contact.feature.domain.model.Contact
import com.pdm.contact.utils.MaskWatcher
import com.pdm.contact.utils.Utils
import com.pdm.contact.utils.Utils.RESULT_CONTACT_ADD
import com.pdm.contact.utils.Utils.RESULT_CONTACT_EDIT
import com.pdm.contact.utils.Utils.contact
import com.pdm.contact.utils.Utils.setBackgroundColor
import com.pdm.contact.utils.Utils.toEditable
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class FormActivity : AppCompatActivity() {

    private val popupWindow by lazy { ListPopupWindow(this, null, android.R.attr.dropDownListViewStyle) }
    private val binding by lazy { ActivityFormBinding.inflate(layoutInflater) }

    private val viewModel by viewModel<FormViewModel>()
    private val items by lazy { Utils.getCountryCodes() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setDataFromIntent()
        setCountryDrawer()
        setObservers()
        setListener()
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiEventState.collectLatest { result ->
                    when (result) {
                        is FormEvent.InputFieldEmailIsInvalid -> {
                            binding.contactFormEmailEditText.error = getString(R.string.error_email)
                        }
                        is FormEvent.InputFieldNameIsInvalid -> {
                            binding.contactFormFullNameEditText.error = getString(R.string.error_name)
                        }
                        is FormEvent.InputFieldNumberIsInvalid -> {
                            binding.contactFormNumberEditText.error = getString(R.string.error_number)
                        }
                        is FormEvent.InputDataIsValid -> {
                            if (intent.contact != null) {
                                updateContact(result.data)
                            } else {
                                insertContact(result.data)
                            }
                        }
                        is FormEvent.InputSuccessfully -> finish()
                    }
                }
            }
        }
    }

    private fun setListener() {
        binding.contactFormNumberEditText.addTextChangedListener(MaskWatcher.buildGlobalNumberPhone())

        binding.contactFormEmailEditText.doAfterTextChanged {
            binding.contactFormEmailEditText.error = null
        }

        binding.contactFormFullNameEditText.doAfterTextChanged {
            binding.contactFormFullNameEditText.error = null
        }

        binding.contactFormNumberEditText.doAfterTextChanged {
            binding.contactFormNumberEditText.error = null
        }

        binding.root.setOnClickListener {
            if (popupWindow.isShowing) popupWindow.dismiss()
        }

        binding.contactFormFullNameEditText.setOnClickListener {
            if (popupWindow.isShowing) popupWindow.dismiss()
        }

        binding.contactFormEmailEditText.setOnClickListener {
            if (popupWindow.isShowing) popupWindow.dismiss()
        }

        binding.contactFormNumberEditText.setOnClickListener {
            if (popupWindow.isShowing) popupWindow.dismiss()
        }

        binding.contactFormCountryEditText.setOnClickListener {
            if (popupWindow.isShowing) popupWindow.dismiss() else popupWindow.show()
            Utils.hideKeyboard(this)
        }

        binding.contactFormAdd.setOnClickListener {
            Utils.hideKeyboard(this)
            doContact()
        }

        binding.contactClose.setOnClickListener {
            Utils.hideKeyboard(this)
            finish()
        }
    }

    private fun setCountryDrawer() {
        popupWindow.setAdapter(ArrayAdapter(this, R.layout.item_menu, items))
        popupWindow.setBackgroundColor(R.drawable.ic_background_menu, this)

        popupWindow.anchorView = binding.contactPopupCountry

        popupWindow.setOnItemClickListener { _, _, position, _ ->
            binding.contactFormCountryEditText.text = items[position].toEditable()
            popupWindow.dismiss()
        }
    }

    private fun doContact() {
        val name = binding.contactFormFullNameEditText.text.toString()
        val email = binding.contactFormEmailEditText.text.toString()
        val number = binding.contactFormNumberEditText.text.toString()
        val country = binding.contactFormCountryEditText.text.toString()
        val id = intent.contact?.id ?: 0

        viewModel.doContact(
            Contact(
                name = name,
                email = email,
                number = number,
                country = country,
                id = id
            )
        )
    }

    private fun setDataFromIntent() {
        binding.contactFormFullNameEditText.setText(intent.contact?.name)
        binding.contactFormEmailEditText.setText(intent.contact?.email)
        binding.contactFormNumberEditText.setText(intent.contact?.number)

        if (intent.contact != null) {
            binding.contactFormCountryEditText.setText(intent.contact?.country)
            binding.contactFormAdd.text = getString(R.string.edit_contact)
        } else {
            binding.contactFormCountryEditText.setText(Utils.getCountryCode())
            binding.contactFormAdd.text = getString(R.string.add_contact)
        }
    }

    private fun insertContact(data: Contact) {
        setResult(RESULT_CONTACT_ADD, Intent().apply { contact = data })
        viewModel.insertContact(data)
    }

    private fun updateContact(data: Contact) {
        setResult(RESULT_CONTACT_EDIT, Intent().apply { contact = data })
        viewModel.updateContact(data)
    }

    override fun onPause() {
        super.onPause()
        Utils.hideKeyboard(this)
    }
}