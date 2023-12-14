package com.salimarizi.sensor.base.activity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.salimarizi.sensor.base.exstorage.ExternalStorageUtils
import com.salimarizi.sensor.base.permission.PermissionUtils

abstract class SensorActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensors: List<Sensor>

    var sensorManager: SensorManager? = null
    var sensorLight: Sensor? = null
    var sensorTemperature: Sensor? = null
    var sensorAccelerometer: Sensor? = null
    var sensorMagnetometer: Sensor? = null
    var sensorStepCounter: Sensor? = null
    var activityDisplay: Display? = null

    lateinit var permissionUtils: PermissionUtils
    lateinit var externalStorageUtils: ExternalStorageUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.let {
            sensors = it.getSensorList(Sensor.TYPE_ALL)
            sensorLight = it.getDefaultSensor(Sensor.TYPE_LIGHT)
            sensorTemperature = it.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            sensorAccelerometer = it.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorMagnetometer = it.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            sensorStepCounter = it.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        }

        activityDisplay = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> display
            else -> (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
        }

        permissionUtils = PermissionUtils(this)
        externalStorageUtils = ExternalStorageUtils(this)

        onCreateView()
    }

    override fun onStart() {
        super.onStart()
        onStartSensor()
    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        onSensorChangeEvent(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    abstract fun onCreateView()
    abstract fun onStartSensor()
    abstract fun onSensorChangeEvent(event: SensorEvent?)

}