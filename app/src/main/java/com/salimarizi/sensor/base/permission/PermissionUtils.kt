package com.salimarizi.sensor.base.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionUtils(private val activity: Activity) {

    fun verifyPermissions(group: PermissionGroup): Boolean {
        if (!isPermissionsGranted(group)) {
            requestPermission(group)
            return false
        }
        return true
    }

    private fun requestPermission(group: PermissionGroup) {
        ActivityCompat.requestPermissions(activity, group.permissions, group.requestCode)
    }

    private fun isPermissionsGranted(group: PermissionGroup): Boolean {
        if (group.permissions.isEmpty()) {
            return false
        }

        var grantedPermission = true
        group.permissions.forEach {
            if (ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED) {
                grantedPermission = false
            }
        }
        return grantedPermission
    }
}