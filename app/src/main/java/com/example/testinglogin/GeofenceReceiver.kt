package com.example.testinglogin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlin.properties.Delegates

class GeofenceReceiver: BroadcastReceiver() {
    lateinit var key: String
    lateinit var message: String
    lateinit var title: String
    lateinit var minutes: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("MYTAG", "Reached GeofenceReceiver!")
        if (context != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)
            val geofencingTransition = geofencingEvent.geofenceTransition
            if (geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                        geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                if (intent != null) {
                    key = intent.getStringExtra("key")!!
                    message = intent.getStringExtra("message")!!
                    title = intent.getStringExtra("title")!!
                    minutes = intent.getStringExtra("minutes")!!
                }
                // I have no idea if this works.
                // MapsActivity.buildGeofence(context, message, title, key, minutes)
                lateinit var notificationChannel: NotificationChannel
                lateinit var builder: Notification.Builder
                val channelId = "com.example.testinglogin"


                Log.i("MYTAG", "NOW 1")
                Log.i("MYTAG", "$key")
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                // Testing notification features.
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationChannel = NotificationChannel(channelId, message, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)
                Log.i("MYTAG", "NOW 2")
                Log.i("MYTAG", "$key")

                // Is the round icon displayed in the notification the app icon?
                builder = Notification.Builder(context, channelId)
                        .setContentTitle(title)
                        .setContentText("Reminder")
                        .setSmallIcon(R.drawable.ic_launcher_round_new)
                        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher))
                        .setContentIntent(pendingIntent)

                Log.i("MYTAG", "NOW 3")
                Log.i("MYTAG", "$key")

                // Here we let the MainActivity know that the reminder can now be displayed:
                var timerList = sharedPreferences.getString("timerList", "")
                var strs = timerList?.split(",")?.toTypedArray()
                var elementCheck = -1
                var counter = 0
                if (strs != null) {
                    while (counter < strs.size) {
                        if (strs[counter] == key) {
                            Log.i("MYTAG","REM $key")
                            elementCheck = counter
                        }
                        counter += 1
                    }
                }
                Log.i("MYTAG", "NOW 4")
                Log.i("MYTAG", "$key")
                if (elementCheck >= 0) {
                    if (strs != null) {
                        val newArray = strs.toMutableList()
                        newArray.removeAt(elementCheck)
                        strs = newArray.toTypedArray()
                    }
                }
                Log.i("MYTAG", "NOW 5")
                Log.i("MYTAG", "$key")
                var arrayString = strs?.joinToString()
                if (arrayString != null) {
                    arrayString = arrayString.replace("\\s".toRegex(), "")
                }
                Log.i("MYTAG", "NOW 6")
                Log.i("MYTAG", "$key")
                Log.i("MYTAG","$arrayString")
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("timerList", arrayString)
                editor.apply()
                Log.i("MYTAG", "$key")
                notificationManager.notify(key.toInt(), builder.build())
            }
        }
    }
}

private fun Intent?.getLongExtra(s: String): Long {
    Log.i("MYTAG", "This should never happen.")
    return 0
}
