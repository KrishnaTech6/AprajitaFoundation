package com.example.aprajitafoundation.ui.activities

import android.R.color.transparent
import android.app.Dialog
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.aprajitafoundation.R
import com.example.aprajitafoundation.utility.isInternetAvailable
import com.example.aprajitafoundation.utility.showSnackBar

open class BaseActivity: AppCompatActivity() {
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private var mProgressDialog: Dialog? = null

    fun showDialogProgress(){
        if (mProgressDialog == null) {
            mProgressDialog = Dialog(this).apply {
                setContentView(R.layout.progress_bar) // Your custom layout
                setCancelable(false) // Optional: make it non-cancelable
                setCanceledOnTouchOutside(false) // Optional: prevent closing on touch outside
                window?.setBackgroundDrawableResource(transparent) // Transparent background
            }
        }
        if (!mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }
    }

    fun handleLoadingState( view: View) {
        if (isInternetAvailable(this)) {
            showDialogProgress()
        } else {
            hideProgressDialog()
            showSnackBar(view, getString(R.string.no_internet_connection))
        }
    }

    fun hideProgressDialog() {
        mProgressDialog?.takeIf { it.isShowing }?.dismiss()
    }
}