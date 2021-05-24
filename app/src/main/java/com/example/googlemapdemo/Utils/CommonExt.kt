package com.example.googlemapdemo.Utils

import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners

fun Context.locationManager() = this.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

fun Context.isGpsEnable(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        locationManager()?.isLocationEnabled == true
    else {
        val gpsMode = Settings.Secure.getInt(
            this.contentResolver,
            Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        gpsMode != Settings.Secure.LOCATION_MODE_OFF
    }

fun View.hideSoftKeyBoard() {
    val inputMethodManager = this.context.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun ImageView.loadImage(url:String){
    Glide.with(context).load(url).transform(
        CenterCrop()
    ).into(this)
}
