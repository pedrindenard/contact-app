package com.pdm.contact.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class RequestPermission(private val activity: AppCompatActivity) {

    private lateinit var registerActivityForResult: ActivityResultLauncher<Array<String>>
    private lateinit var requestListener: RequestListener

    private val permissions = arrayOf(Manifest.permission.CALL_PHONE)
    private var positionAdapter: Int = 0

    fun registerActivityForResult() {
        registerActivityForResult = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            if (result.all { it.value }) {
                requestListener.onPermissionGranted(positionAdapter)
            } else {
                requestListener.onPermissionDenied()
            }
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun checkPermission(position: Int) {
        if (!hasPermissions(permissions)) {
            registerActivityForResult.launch(permissions)
            positionAdapter = position
        } else {
            requestListener.onPermissionGranted(position)
        }
    }

    fun setRequestPermissionListener(listener: RequestListener) {
        requestListener = listener
    }

    interface RequestListener {
        fun onPermissionDenied()
        fun onPermissionGranted(position: Int)
    }
}