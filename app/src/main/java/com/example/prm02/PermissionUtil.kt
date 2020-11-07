package com.example.prm10

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

class PermissionUtil(private val activity: AppCompatActivity) {
    companion object {
        const val PERMISSION_REQ_CODE = 12
    }

    val hasBgPermission get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        activity.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    else true

    val hasLocationFinePremission get() =
        activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    fun checkLocationPremissions(reqIfNot: Boolean = true): Boolean {
        val permissionArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        else
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        return (hasBgPermission && hasLocationFinePremission).also {
            if (!it) {
                activity.requestPermissions(
                    permissionArray,
                    PERMISSION_REQ_CODE
                )
            }
        }
    }
}