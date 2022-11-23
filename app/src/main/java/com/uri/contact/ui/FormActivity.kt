package com.uri.contact.ui

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.uri.contact.R
import com.uri.contact.Utils
import com.uri.contact.databinding.ActivityFormBinding
import com.uri.contact.model.Contact
import com.uri.contact.model.ContactEntity
import com.uri.contact.room.DatabaseImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormActivity : AppCompatActivity() {

    private val extra by lazy { intent.getSerializableExtra("contact") as? Contact }
    private val binding by lazy { ActivityFormBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListener()
        setPreData()
    }

    private fun setPreData() {
        binding.contactFormFullNameEditText.setText(extra?.name)
        binding.contactFormEmailEditText.setText(extra?.email)
        binding.contactFormNumberEditText.setText(extra?.number)

        if (extra != null) {
            binding.contactFormAdd.text = getString(R.string.edit_contact)
        } else {
            binding.contactFormAdd.text = getString(R.string.add_contact)
        }
    }

    private fun setListener() {
        binding.contactFormAdd.setOnClickListener {
            val name = binding.contactFormFullNameEditText.text.toString()
            val email = binding.contactFormEmailEditText.text.toString()
            val number = binding.contactFormNumberEditText.text.toString()
            val id = extra?.id ?: 0

            if (name.isEmpty() || name.isBlank() || name.length < 3) {
                showErrorMessage(getString(R.string.error_name))
                return@setOnClickListener
            }

            if (email.isEmpty() || email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorMessage(getString(R.string.error_email))
                return@setOnClickListener
            }

            if (number.isEmpty() || number.isBlank() || PhoneNumberUtils.isGlobalPhoneNumber(number)) {
                showErrorMessage(getString(R.string.error_number))
                return@setOnClickListener
            }

            connectDao(id, name, email, number)
            setActivityResult(id, name, email, number)
        }

        binding.contactClose.setOnClickListener {
            finish()
        }
    }

    private fun connectDao(id: Int, name: String, email: String, number: String) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseImpl.getInstance(this@FormActivity).apply {
                val contact = ContactEntity(id, name, email, number)
                if (extra != null) {
                    dao().update(contact)
                } else {
                    dao().insert(contact)
                }
            }
        }
    }

    private fun setActivityResult(id: Int, name: String, email: String, number: String) {
        val intent = Intent().putExtra("data", Contact(id, name, email, number))
        if (extra != null) {
            setResult(Utils.RESULT_CONTACT_EDIT, intent)
            finish()
        } else {
            setResult(Utils.RESULT_CONTACT_ADD, intent)
            finish()
        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}