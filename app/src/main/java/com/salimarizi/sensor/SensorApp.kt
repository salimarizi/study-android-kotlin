package com.salimarizi.sensor

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class SensorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}