package com.example.aprajitafoundation.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
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
import com.example.aprajitafoundation.BuildConfig
import com.google.android.material.snackbar.Snackbar
import org.bouncycastle.crypto.params.Blake3Parameters.context


fun saveInputToPreferences(context: Context, key: String, value: String) {
    //Shared preference
    val sharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(key, value)
    editor.apply()
}

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
    Snackbar.make(view, text,Snackbar.LENGTH_SHORT ).show()
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
            config["cloud_name"] = BuildConfig.CLOUD_NAME
            config["api_key"] = BuildConfig.CLOUD_API_KEY
            config["api_secret"] = BuildConfig.CLOUD_API_KEY_SECRET
            MediaManager.init(context, config)
            isInitialized = true
        }
    }
}

fun uploadToCloudinary(context: Context, uri: Uri, view: View,progressReport:(Int)-> Unit={}, onStart:()->Unit ={}  , onSuccess: (String) -> Unit) {
    val transformation = Transformation<Transformation<*>>()
        .quality("auto:best") // Automatically adjust the quality
        .fetchFormat("auto") // Automatically determine the best format
        .crop("limit")
        .height(1920).width(1920)

    MediaManager.get().upload(uri)
        .option("transformation", transformation) // Apply the transformation
        .callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                view.visibility = View.VISIBLE
                onStart()
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                val progress = (bytes.toFloat() / totalBytes.toFloat() * 100).toInt()
                progressReport(progress)
                // Upload progress
                if (!isInternetAvailable(context)) {
                    view.visibility = View.GONE
                    Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                view.visibility = View.GONE
                val secureUrl = resultData["secure_url"].toString()
                Log.d("Cloud", "URL: $secureUrl")
                onSuccess(secureUrl)
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                // Handle error
                view.visibility = View.GONE
                Toast.makeText(context, "Error: ${error.description}", Toast.LENGTH_SHORT).show()
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                // Handle reschedule
            }
        })
        .dispatch()
}

