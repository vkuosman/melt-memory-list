package com.example.testinglogin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.TimeUnit

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val KEY_TITLE = "key_title"
        const val KEY_NOTE = "key_note"
        const val KEY_LIST = "key_list"
        const val KEY_NUM = "key_num"
        const val KEY_MIN = "key_min"
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val start = LatLng(65.0, 35.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start))

        longClick(mMap)
    }
    private fun longClick(googleMap: GoogleMap) {
        googleMap.setOnMapLongClickListener {
            googleMap.addMarker(
                MarkerOptions().position(it)
            )
            val X = it.latitude
            val Y = it.longitude
            val title = intent.getStringExtra("TitleTest")
            val note = intent.getStringExtra("NoteTest")
            val counter = intent.getStringExtra("CounterStr")
            val minutes = intent.getStringExtra("DiffMinutes")?.toLong()

            val context = this
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            var db = DataBaseHandler(context)
            if (title != null && note!= null && counter != null && minutes != null) {
                var rem = ReminderClass(title, note, counter, 0, X.toString(), Y.toString())
                // Here we insert the data to the database
                db.insertData(rem)
                editor.putInt("memoCounter", counter.toInt() + 1)
                editor.apply()

                // Setting up the notification
                setOneTimeNotification(title, note, counter, minutes)
            }

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
    private fun setOneTimeNotification(title: String, note: String, counter: String, minutes: Long) {
        val data: Data = Data.Builder()
            .putString(MapsActivity.KEY_TITLE, title)
            .putString(MapsActivity.KEY_NOTE, note)
            .putString(MapsActivity.KEY_NUM, counter)
            .build()
        val notificationRequest: OneTimeWorkRequest = OneTimeWorkRequest
            .Builder(ReminderWorker::class.java)
            .addTag(counter)
            .setInputData(data)
            .setInitialDelay(minutes, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(notificationRequest)

        // Setting the reminder information
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // Here we retrieve the list of reminders with ongoing timers
        var timerList = sharedPreferences.getString("timerList", "")
        if (timerList.isNullOrEmpty() || timerList.isBlank()){
            editor.putString("timerList", counter)
        } else {
            timerList = timerList + ",$counter"
            editor.putString("timerList", timerList)
        }
        editor.apply()
    }
}