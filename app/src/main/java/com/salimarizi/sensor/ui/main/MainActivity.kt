package com.salimarizi.sensor.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Handler
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.salimarizi.sensor.R
import com.salimarizi.sensor.base.activity.SensorActivity
import com.salimarizi.sensor.databinding.ActivitySensorListenerBinding
import java.util.Date

class MainActivity : SensorActivity()  {

    private val handler = Handler()
    private var currentTemperature = 0.0f
    private var currentLight = 0.0f
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var longitude = 0.0f
    private var latitude = 0.0f

    companion object {
        const val INTENT_TITLE = "intent-title"
    }

    private val bindView: ActivitySensorListenerBinding by lazy {
        ActivitySensorListenerBinding.inflate(layoutInflater)
    }

    override fun onCreateView() {
        setContentView(bindView.root)
        setSupportActionBar(bindView.toolbarLayout.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = intent?.getStringExtra(INTENT_TITLE)
        }

        sensorLight?.let {
            bindView.sensorLight.text = getString(R.string.started_sensor_light)
        }
        sensorTemperature?.let {
            bindView.sensorProximity.text = getString(R.string.started_sensor_temperature)
        }
        bindView.longitudeLatitude.text = getString(R.string.started_longitude_latitude)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()

        handler.postDelayed(object : Runnable {
            override fun run() {
                recordToFile()
                handler.postDelayed(this, 1000) // 1000 milliseconds = 1 second
            }
        }, 1000) // Initial delay, in this case, it starts immediately

        bindView.btnMaps.setOnClickListener{
            openGoogleMaps()
        }
    }

    override fun onStartSensor() {
        sensorLight?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        sensorTemperature?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChangeEvent(event: SensorEvent?) {
        val sensorType = event?.sensor?.type
        val currentValue = event?.values?.get(0)

        when (sensorType) {
            Sensor.TYPE_LIGHT -> {
                bindView.sensorLight.text = getString(R.string.label_light, currentValue)
                currentLight = currentValue ?: 0.0f
            }

            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                bindView.sensorProximity.text =
                    getString(R.string.label_proximity, currentValue)
                currentTemperature = currentValue ?: 0.0f
            }
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations, this can be null.
                    if (location != null) {
                        latitude = location.latitude.toFloat()
                        longitude = location.longitude.toFloat()

                        bindView.longitudeLatitude.text = "Location (Long, Lat): $longitude, $latitude"
                    }
                }
        }
    }

    private fun recordToFile() {
        val timestamp = Date().time
        requestLocationUpdates()

        val recordData = "{\"Temp\": $currentTemperature, \"Light\": $currentLight, " +
                "\"Time\": $timestamp, \"Location\": {\"Long\":$longitude,\"Lat\":$latitude}},"
        externalStorageUtils.createFile("record.txt", recordData)
    }

    private fun openGoogleMaps() {
        // Open Google Maps with the current location
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Handle the case where Google Maps is not installed
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.google.android.apps.maps")
            )
            if (playStoreIntent.resolveActivity(packageManager) != null) {
                startActivity(playStoreIntent)
            } else {
                // If the Play Store app is not available, open the Play Store website
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
                    )
                )
            }
        }
    }
}