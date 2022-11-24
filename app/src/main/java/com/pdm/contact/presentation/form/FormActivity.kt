package com.pdm.contact.presentation.form

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class FormActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFormBinding.inflate(layoutInflater) }
    private val viewModel by viewModel<FormViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setDataFromIntent()
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
        binding.contactFormEmailEditText.doAfterTextChanged {
            binding.contactFormEmailEditText.error = null
        }

        binding.contactFormFullNameEditText.doAfterTextChanged {
            binding.contactFormFullNameEditText.error = null
        }

        binding.contactFormNumberEditText.doAfterTextChanged {
            binding.contactFormNumberEditText.error = null
        }

        binding.contactFormNumberEditText.addTextChangedListener(MaskWatcher.buildGlobalNumberPhone())

        binding.contactFormAdd.setOnClickListener {
            Utils.hideKeyboard(this)
            doContact()
        }

        binding.contactClose.setOnClickListener {
            Utils.hideKeyboard(this)
            finish()
        }
    }

    private fun doContact() {
        val name = binding.contactFormFullNameEditText.text.toString()
        val email = binding.contactFormEmailEditText.text.toString()
        val number = binding.contactFormNumberEditText.text.toString()
        val id = intent.contact?.id ?: 0

        viewModel.doContact(
            Contact(
                name = name,
                email = email,
                number = number,
                id = id
            )
        )
    }

    private fun setDataFromIntent() {
        binding.contactFormFullNameEditText.setText(intent.contact?.name)
        binding.contactFormEmailEditText.setText(intent.contact?.email)
        binding.contactFormNumberEditText.setText(intent.contact?.number)

        if (intent.contact != null) {
            binding.contactFormAdd.text = getString(R.string.edit_contact)
        } else {
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