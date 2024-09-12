package com.example.aprajitafoundation.utility

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.cloudinary.Transformation
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.aprajitafoundation.R
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

 fun saveInputToPreferences(context: Context, key: String, value: String) {
    //Shared preference
    val sharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(key, value)
    editor.apply()
}

fun EditText.afterTextChanged(action: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            s?.let { action(it.toString()) }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

object CloudinaryManager{
    private var isInitialized = false
    fun initCloudinary(context: Context) {
        if (!isInitialized) {
            val config: HashMap<String, String> = HashMap()
            config["cloud_name"] = Constants.cloudName
            config["api_key"] = Constants.cloudApiKey
            config["api_secret"] = Constants.cloudApiKeySecret
            MediaManager.init(context, config)
            isInitialized = true
        }
    }
}

fun uploadToCloudinary(context: Context, filePath: String, progressBar: ProgressBar, onSuccess: (String) -> Unit) {
    val transformation = Transformation<Transformation<*>>()
        .quality("auto:best") // Automatically adjust the quality
        .fetchFormat("auto") // Automatically determine the best format
        .crop("limit")
        .height(1920).width(1920)

    MediaManager.get().upload(filePath)
        .option("transformation", transformation) // Apply the transformation
        .callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                // Upload progress
                if (!isInternetAvailable(context)) {
                    progressBar.visibility = View.GONE
                    showToast(context, "No Internet Connection!")
                }
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                progressBar.visibility = View.GONE
                val secureUrl = resultData["secure_url"].toString()
                Log.d("Cloud", "URL: $secureUrl")
                onSuccess(secureUrl)
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                // Handle error
                progressBar.visibility = View.GONE
                showToast(context, "Error: ${error.description}")
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                // Handle reschedule
            }
        })
        .dispatch()
}

