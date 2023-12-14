package com.salimarizi.sensor.base.permission

import android.Manifest

enum class PermissionGroup(val requestCode: Int, val permissions: Array<String>) {

    ACTIVITY_RECOGNITION(1, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION)),
    EXTERNAL_STORAGE(
        2,
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

}