package com.example.prm02

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class MyLoc(activity: MainActivity) {

    var RADIUS: Float = 1000f //w metrach

    val activity = activity

    lateinit var last_loc:Location

    companion object{
        lateinit var  instance: MyLoc
    }

    init {
        instance = this


    }

    val geocoder by lazy { Geocoder(activity) }

    val geofences by lazy { LocationServices.getGeofencingClient(activity) }

    val locManager by lazy { LocationServices.getFusedLocationProviderClient(activity) }

    lateinit var locationRequest: LocationRequest

    val locCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
        }
    }

    init {
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
        }
        locManager.requestLocationUpdates(locationRequest,locCallback,null)

        locManager.lastLocation.addOnSuccessListener {
            this.last_loc = it
        }


    }


    fun getLastLocation():Location{
        return last_loc
    }

    fun getLocationText(location: Location):String{
        return geocoder.getFromLocation(location.latitude,location.longitude,1)[0].locality
    }

    fun registerGeofence(location: Location, requestId: String){
        val geofence = Geofence.Builder().setRequestId(requestId).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).setCircularRegion(location.longitude,location.latitude,this.RADIUS).setExpirationDuration(Geofence.NEVER_EXPIRE).build()

        val geoReq = GeofencingRequest.Builder().addGeofence(geofence).setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build()

        var penIn = PendingIntent.getForegroundService(activity,4, Intent(activity,LocationNotificationService::class.java),PendingIntent.FLAG_UPDATE_CURRENT)
        geofences.addGeofences(geoReq,penIn)




    }


}