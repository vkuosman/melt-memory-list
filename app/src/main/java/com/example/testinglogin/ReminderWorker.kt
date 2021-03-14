package com.example.testinglogin

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng


class ReminderWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    // Notification Manager
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    val channelId = "com.example.testinglogin"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {

        val newTitle = inputData.getString(MapsActivity.KEY_TITLE)
        val newNote = inputData.getString(MapsActivity.KEY_NOTE)
        val keyCounter = inputData.getString(MapsActivity.KEY_NUM)
        val X = inputData.getString(MapsActivity.KEY_X)
        val Y = inputData.getString(MapsActivity.KEY_Y)
        val minutes = inputData.getString(MapsActivity.KEY_MIN)
        var location = LatLng(X!!.toDouble(), Y!!.toDouble())

        lateinit var geofencingClient: GeofencingClient
        geofencingClient = LocationServices.getGeofencingClient(applicationContext)
        Log.i("MYTAG", "doWork: $keyCounter")
        createGeofence(location, newTitle!!, newNote!!, keyCounter!!, minutes!!, geofencingClient)
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(title: String, note: String, remCounter: String) {

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)

        // Testing notification features.
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationChannel = NotificationChannel(channelId, note, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)

        // Is the round icon displayed in the notification the app icon?
        builder = Notification.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText("Reminder")
            .setSmallIcon(R.drawable.ic_launcher_round_new)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_launcher))
            .setContentIntent(pendingIntent)

        // Here we let the MainActivity know that the reminder can now be displayed:
        var timerList = sharedPreferences.getString("timerList", "")
        var strs = timerList?.split(",")?.toTypedArray()
        var elementCheck = -1
        var counter = 0
        if (strs != null) {
             while (counter < strs.size) {
                if (strs[counter] == remCounter) {
                    Log.i("MYTAG","REM $remCounter")
                    elementCheck = counter
                }
                 counter += 1
            }
        }
        if (elementCheck >= 0) {
            if (strs != null) {
                val newArray = strs.toMutableList()
                newArray.removeAt(elementCheck)
                strs = newArray.toTypedArray()
            }
        }
        var arrayString = strs?.joinToString()
        if (arrayString != null) {
            arrayString = arrayString.replace("\\s".toRegex(), "")
        }
        Log.i("MYTAG","$arrayString")
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("timerList", arrayString)
        editor.apply()
        notificationManager.notify(remCounter.toInt(), builder.build())

    }

    private fun createGeofence(
            location: LatLng,
            title: String,
            note: String,
            counter: String,
            minutes: String,
            geofencingClient: GeofencingClient ) {
        Log.i("MYTAG", "Starting to create a geofence!")
        Log.i("MYTAG", "Counter in worker $counter")
        val geofence = Geofence.Builder()
                .setRequestId(GEOFENCE_ID)
                .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
                .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        or Geofence.GEOFENCE_TRANSITION_DWELL)
                . setLoiteringDelay(GEOFENCE_DWELL_DELAY)
                .build()

        val geofenceRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

        Log.i("MYTAG", "INTENT")
        val intent = Intent(applicationContext, GeofenceReceiver::class.java)
                .putExtra("title", title)
                .putExtra("message", note)
                .putExtra("key", counter)
                .putExtra("minutes", minutes)


        val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Have to make sure this is checked before?
            return
        }
        geofencingClient.addGeofences(geofenceRequest, pendingIntent)
    }
}