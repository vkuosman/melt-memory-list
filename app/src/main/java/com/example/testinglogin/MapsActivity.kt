package com.example.testinglogin

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.TimeUnit

const val LOCATION_REQUEST_CODE = 123
const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
const val CAMERA_ZOOM_LEVEL = 13f
const val GEOFENCE_RADIUS = 300
const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
const val GEOFENCE_EXPIRATION = 365 * 24 * 60 * 60 * 1000 // Placeholder. Most likely removed later.
const val GEOFENCE_DWELL_DELAY = 10 * 1000 // 10 seconds

private val TAG = MapsActivity::class.java.simpleName

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    companion object {
        const val KEY_TITLE = "key_title"
        const val KEY_NOTE = "key_note"
        const val KEY_LIST = "key_list"
        const val KEY_NUM = "key_num"
        const val KEY_MIN = "key_min"
        const val KEY_X = "key_x"
        const val KEY_Y = "key_y"
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?) {

        // Setting the reminder information
        val counter = intent.getStringExtra("CounterStr")
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Here we retrieve the list of reminders with ongoing timers
        var timerList = sharedPreferences.getString("timerList", "")
        if (timerList.isNullOrEmpty() || timerList.isBlank()){
            editor.putString("timerList", counter)
        } else {
            timerList = "$timerList,$counter"
            editor.putString("timerList", timerList)
        }
        editor.apply()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("MYTAG", "Reached MapsActivity")
        map = googleMap

        if (!isLocationPermissionGranted()) {
            val permissions = mutableListOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                LOCATION_REQUEST_CODE
            )
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if(it != null) {
                    with(map) {
                        val latlng = LatLng(it.latitude, it.longitude)
                        moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, CAMERA_ZOOM_LEVEL))
                    }
                } else {
                    with(map) {
                        moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(65.01355, 24.46402),
                                CAMERA_ZOOM_LEVEL
                            )
                        )
                    }
                }
            }
        }
        longClick(map)
    }
    private fun longClick(googleMap: GoogleMap) {
        googleMap.setOnMapLongClickListener {
            googleMap.addMarker(
                MarkerOptions().position(it)
            )
            map.addCircle(
                CircleOptions()
                    .center(it)
                    .strokeColor(Color.argb(50, 70, 70, 70))
                    .fillColor(Color.argb(70, 150, 150, 150))
                    .radius(GEOFENCE_RADIUS.toDouble())
            )

            val X = it.latitude
            val Y = it.longitude
            val title = intent.getStringExtra("TitleTest")
            val note = intent.getStringExtra("NoteTest")
            val counter = intent.getStringExtra("CounterStr")
            val minutes = intent.getStringExtra("DiffMinutes")

            val context = this
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            var db = DataBaseHandler(context)

            val data: Data = Data.Builder()
                    .putString(KEY_TITLE, title)
                    .putString(KEY_NOTE, note)
                    .putString(KEY_NUM, counter)
                    .putString(KEY_X, X.toString())
                    .putString(KEY_Y, Y.toString())
                    .putString(KEY_MIN, minutes)
                    .build()
            val notificationRequest: OneTimeWorkRequest = OneTimeWorkRequest
                    .Builder(ReminderWorker::class.java)
                    .addTag(counter!!)
                    .setInputData(data)
                    .setInitialDelay(minutes!!.toLong(), TimeUnit.MINUTES)
                    .build()
            Log.i("MYTAG", "Giving work request:")
            WorkManager.getInstance(context).enqueue(notificationRequest)

            if (title != null && note!= null && counter != null && minutes != null) {
                var rem = ReminderClass(title, note, counter, 0, X.toString(), Y.toString())
                // Here we insert the data to the database
                db.insertData(rem)
                editor.putInt("memoCounter", counter.toInt() + 1)
                editor.apply()

            }

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        if (requestCode == GEOFENCE_LOCATION_REQUEST_CODE) {
            if (permissions.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Request permissions again. Separate to own function if enough time.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            applicationContext,
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            GEOFENCE_LOCATION_REQUEST_CODE
                        )
                    }
                }
            }
        }
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && (
                        grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                                grantResults[1] == PackageManager.PERMISSION_GRANTED
                    )
            ) {
                map.isMyLocationEnabled = true
                onMapReady(map)
            } else {
                // Request permissions again. Separate to own function if enough time.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            applicationContext,
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            GEOFENCE_LOCATION_REQUEST_CODE
                        )
                    }
                }
            }
        }
    }
}