package com.pdm.contact.presentation.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdm.contact.R
import com.pdm.contact.databinding.ActivityContactBinding
import com.pdm.contact.feature.domain.LocalEvent
import com.pdm.contact.presentation.form.FormActivity
import com.pdm.contact.utils.RequestPermission
import com.pdm.contact.utils.Utils
import com.pdm.contact.utils.Utils.addOnScrollHiddenView
import com.pdm.contact.utils.Utils.contact
import com.pdm.contact.utils.Utils.getParcelable
import com.pdm.contact.utils.Utils.setParcelable
import com.pdm.contact.utils.Utils.startOpenAnimation
import com.pdm.contact.utils.Utils.startToastMessage
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactActivity : AppCompatActivity() {

    private val binding by lazy { ActivityContactBinding.inflate(layoutInflater) }
    private val requestPermission by lazy { RequestPermission(this) }

    private val mainAdapter by lazy { ContactAdapter() }
    private val viewModel by viewModel<ContactViewModel>()

    private val activityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Utils.RESULT_CONTACT_ADD -> {
                val data = result.data?.contact
                if (data != null) {
                    mainAdapter.insertItem(data)
                    viewModel.insertItemOnBackupContact(data)
                    binding.contactEmpty.isVisible = mainAdapter.items.isEmpty()
                }
            }
            Utils.RESULT_CONTACT_EDIT -> {
                val data = result.data?.contact
                if (data != null) {
                    mainAdapter.updateItem(data)
                    viewModel.updateItemOnBackupContact(data)
                    binding.contactEmpty.isVisible = mainAdapter.items.isEmpty()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.contactRecyclerView.setParcelable(outState, Utils.RV_INSTANCE)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.contactRecyclerView.getParcelable(savedInstanceState, Utils.RV_INSTANCE)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewModel.getContacts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission.registerActivityForResult()
        setContentView(binding.root)
        setObservers()
        setListeners()
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiEventState.collectLatest { result ->
                    when (result) {
                        is LocalEvent.RemoveItem -> { /* TODO: Not used in screen`s */ }
                        is LocalEvent.Empty -> binding.contactEmpty.visibility = View.VISIBLE
                        is LocalEvent.Failure -> binding.contactEmpty.visibility = View.VISIBLE
                        is LocalEvent.Success -> {
                            mainAdapter.insertItems(result.data)
                            binding.contactEmpty.isVisible = mainAdapter.items.isEmpty()
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiRemoveEventState.collectLatest { position ->
                    mainAdapter.removeItem(position)
                    binding.contactEmpty.isVisible = mainAdapter.items.isEmpty()
                    binding.contactCardSearch.startOpenAnimation()
                }
            }
        }
    }

    private fun setListeners() {
        binding.contactRecyclerView.adapter = mainAdapter
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.contactRecyclerView.addOnScrollHiddenView(binding.contactCardSearch)

        mainAdapter.setOnClickListener(object : ContactAdapter.ItemClickListener {
            override fun onEditClick(position: Int) {
                startActivityContact(position)
            }

            override fun onDeleteClick(position: Int) {
                createDialogAlert(position)
            }

            override fun onCallClick(position: Int) {
                requestPermission.checkPermission(position)
            }

            override fun onShareClick(position: Int) {
                startShareContact(position)
            }
        })

        requestPermission.setRequestPermissionListener(object : RequestPermission.RequestListener {
            override fun onPermissionDenied() {
                startToastMessage(getString(R.string.error_permission))
            }

            override fun onPermissionGranted(position: Int) {
                startPhoneCall(position)
            }
        })

        binding.contactSearch.doAfterTextChanged { text ->
            if (text != null && text.length > 1) {
                mainAdapter.cleanItems()
                mainAdapter.insertItems(viewModel.contactBackup)
                mainAdapter.filterItems(text.toString())
            } else {
                mainAdapter.cleanItems()
                mainAdapter.insertItems(viewModel.contactBackup)
            }
        }

        binding.contactAdd.setOnClickListener {
            startActivityContact()
        }
    }

    private fun createDialogAlert(position: Int) {
        AlertDialog.Builder(this).apply {
            setMessage(R.string.alert_dialog_message)

            setPositiveButton(R.string.alert_dialog_yes) { dialog, _ ->
                viewModel.deleteContact(mainAdapter.items[position].id, position)
                dialog.dismiss()
            }

            setNegativeButton(R.string.alert_dialog_no) { dialog, _ ->
                dialog.dismiss()
            }
            create()
        }.show()
    }

    private fun startPhoneCall(position: Int) {
        val contact = mainAdapter.items[position]

        val country = contact.country.replace(" ", "")
        val phone = contact.number.replace(" ", "")

        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$country$phone"))
        startActivity(intent)
    }

    private fun startShareContact(position: Int) {
        val contact = mainAdapter.items[position]
        val intent = Intent(Intent.ACTION_SEND)
        val extra = "${contact.name}\n${contact.country} ${contact.number}\n${contact.email}"

        intent.putExtra(Intent.EXTRA_TEXT, extra)
        intent.type = "text/plain"

        startActivity(intent)
    }

    private fun startActivityContact() {
        val intent = Intent(this, FormActivity::class.java)
        activityResult.launch(intent)
    }

    private fun startActivityContact(position: Int) {
        val intent = Intent(this, FormActivity::class.java)
        intent.contact = mainAdapter.items[position]
        activityResult.launch(intent)
    }

    override fun onPause() {
        super.onPause()
        Utils.hideKeyboard(this)
    }
}