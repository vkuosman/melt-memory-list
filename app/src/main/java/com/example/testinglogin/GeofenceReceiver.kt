package com.example.testinglogin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlin.properties.Delegates

class GeofenceReceiver: BroadcastReceiver() {
    lateinit var key: String
    lateinit var message: String
    lateinit var title: String
    lateinit var minutes: String

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
                // Retrieve from database... what, why?
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                var db = DataBaseHandler(context)
                val data = db.readData()

                // I have no idea if this works.
                MapsActivity.showNotification(context, message, title, key, minutes)
            }
        }
    }
}

private fun Intent?.getLongExtra(s: String): Long {
    Log.i("MYTAG", "This should never happen.")
    return 0
}
