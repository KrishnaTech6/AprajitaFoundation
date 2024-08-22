package com.example.aprajitafoundation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}


fun showSnackBar(view: View,text:String){
    Snackbar.make(view, text,Snackbar.LENGTH_SHORT ).setBackgroundTint(Color.RED).show()
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
private lateinit var mProgressDialog: Dialog

fun showDialogProgress(context: Context){
    mProgressDialog = Dialog(context)
    mProgressDialog.setContentView(R.layout.progress_bar)
    mProgressDialog.setCancelable(false)
    mProgressDialog.setCanceledOnTouchOutside(false)
    mProgressDialog.show()
}

fun hideProgressDialog() = mProgressDialog.dismiss()