package com.example.prm02

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.GeofencingEvent

class LocationNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {



        var noteID_fromList = GeofencingEvent.fromIntent(intent).triggeringGeofences[0].requestId.toInt()
        var item_intent = Intent(this,ItemActivity::class.java)
        item_intent.putExtra("item_id",noteID_fromList)
        item_intent.putExtra("mode","edit")
        var pendingIntent = PendingIntent.getForegroundService(this,5,item_intent,0)
        var not = NotificationCompat.Builder(this,"com.example.prm02.channels.mainCh")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Watch your photo memo")
            .setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        //var notManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //notManager.notify(0,not)





        startForeground(1,not)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}