package com.uri.contact.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.uri.contact.R
import com.uri.contact.Utils
import com.uri.contact.adapter.ContactAdapter
import com.uri.contact.databinding.ActivityContactBinding
import com.uri.contact.model.Contact
import com.uri.contact.room.DatabaseImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactActivity : AppCompatActivity() {

    private val mainAdapter by lazy { ContactAdapter() }
    private val binding by lazy { ActivityContactBinding.inflate(layoutInflater) }

    private val activityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Utils.RESULT_CONTACT_EDIT -> {
                val data = result.data?.getSerializableExtra("data") as? Contact
                if (data != null) {
                    mainAdapter.updateItems(data)
                }
            }
            Utils.RESULT_CONTACT_ADD -> {
                val data = result.data?.getSerializableExtra("data") as? Contact
                if (data != null) {
                    mainAdapter.insertItem(data)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setItemOnAdapter()
        setListener()
        setAdapter()
    }

    private fun setAdapter() {
        binding.contactRecyclerView.adapter = mainAdapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)

        mainAdapter.setOnClickListener(object : ContactAdapter.ItemClickListener {
            override fun onEditClick(position: Int) {
                startActivityContact(position)
            }

            override fun onDeleteClick(position: Int) {
                createDialogAlert(position)
            }

            override fun onCallClick(position: Int) {
                startPhoneCall(position)
            }
        })
    }

    private fun setListener() {
        binding.contactAdd.setOnClickListener {
            startActivityContact()
        }
    }

    private fun deleteContact(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseImpl.getInstance(this@ContactActivity).apply {
                dao().delete(mainAdapter.items[position].id)
                CoroutineScope(Dispatchers.Main).launch {
                    mainAdapter.removeItem(position)
                    binding.contactEmpty.isVisible = mainAdapter.items.isEmpty()
                }
            }
        }
    }

    private fun setItemOnAdapter() {
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseImpl.getInstance(this@ContactActivity).apply {
                val array = dao().getAll()?.map { it.contact } ?: emptyList()
                CoroutineScope(Dispatchers.Main).launch {
                    mainAdapter.insertItems(array)
                    binding.contactEmpty.isVisible = mainAdapter.items.isEmpty()
                }
            }
        }
    }

    private fun createDialogAlert(position: Int) {
        AlertDialog.Builder(this).apply {
            setMessage(R.string.alert_dialog_message)

            setPositiveButton(R.string.alert_dialog_yes) { dialog, _ ->
                deleteContact(position)
                dialog.dismiss()
            }

            setNegativeButton(R.string.alert_dialog_no) { dialog, _ ->
                dialog.dismiss()
            }
            create()
        }.show()
    }

    private fun startPhoneCall(position: Int) {
        val numberPhone = mainAdapter.items[position].number
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numberPhone"))
        startActivity(intent)
    }

    private fun startActivityContact() {
        val intent = Intent(this, FormActivity::class.java)
        activityResult.launch(intent)
    }

    private fun startActivityContact(position: Int) {
        val intent = Intent(this, FormActivity::class.java)
        intent.putExtra("contact", mainAdapter.items[position])
        activityResult.launch(intent)
    }
}