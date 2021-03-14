package com.example.testinglogin

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat

lateinit var sharedPreferences: SharedPreferences

// The current implementation is meant to demonstrate ListView with placeholder messages
class MainActivity : AppCompatActivity() {

    lateinit var locationManager: LocationManager
    private var checkGps = false
    private var checkNetwork = false
    private var gpsLocation: Location? = null
    private var networkLocation: Location? = null
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private val PERMISSION_REQUEST = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        // Testing location check
        var targetlocation = Location(LocationManager.GPS_PROVIDER)
        targetlocation.latitude = 55.555555
        targetlocation.longitude = 55.555555


        Log.i("MYTAG","Now at onCreate MainActivity")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        val memButton = findViewById<Button>(R.id.memoButtonMain)
        val editButton = findViewById<Button>(R.id.editButtonMain)
        val refButton = findViewById<Button>(R.id.refreshButton)
        val numInput = findViewById<EditText>(R.id.numInputMain)
        val imgClick = findViewById<ImageView>(R.id.imageView)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val listView = findViewById<ListView>(R.id.MemoryList)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheck(permissions)) {
                // TESTING
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }

        // Here we check which reminders should be hidden (timer)
        var timerList = sharedPreferences.getString("timerList", "")
        val strs = timerList?.split(",")?.toTypedArray()

        val context = this

        // Here we get the data from the database and insert it to the arrays.
        // This is done so that our older ListView implementation can be used.
        // May be streamlined in the future.

        var db = DataBaseHandler(context)
        var data = db.readData()
        var titleArray = arrayListOf<String>()
        var messArray = arrayListOf<String>()
        var idArray = arrayListOf<Int>()
        val viewCheck = sharedPreferences.getString("viewCheck", "0")

        Log.i("MYTAG","The viewing setting is $viewCheck")

        for (i in 0..data.size - 1) {
            var checker = true
            if (strs != null) {
                for (s in strs) {
                    if (s == data.get(i).id) {
                        Log.i("MYTAG","#$s has been hidden in MainActivity")
                        checker = false

                    }
                }
            }
            if (checker || viewCheck == "1") {
                titleArray.add(data.get(i).name)
                messArray.add(data.get(i).mess)
            }
        }

        Log.i("MYTAG","Necessary notifications should now be hidden: $timerList")

        listView.adapter = CustomAdapter(this, titleArray, messArray, idArray)

        memButton.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            finish()
            startActivity(intent)
        }

        refButton.setOnClickListener {
            val newView = sharedPreferences.getString("viewCheck", "0")
            if (newView == "0") {
                Log.i("MYTAG","The viewing settings have been adjusted: 1")
                editor.putString("viewCheck", "1")
            } else {
                Log.i("MYTAG","The viewing setting have been adjusted: 0")
                editor.putString("viewCheck", "0")
            }
            editor.apply()
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }

        imgClick.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            finish()
            startActivity(intent)
        }

        editButton.setOnClickListener {
            val numIn: String = numInput.text.toString()
            if (numIn.isBlank() || numIn.isBlank()) {

                Toast.makeText(context, "Please give the number of the reminder you wish to edit.", Toast.LENGTH_SHORT).show()

            } else {

                val finalNumIn = numIn.toInt()
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("tempEditNum", finalNumIn)
                editor.apply()
                val intent = Intent(this, EditActivity::class.java)
                finish()
                startActivity(intent)

            }
        }
    }

    private fun permissionCheck(permissionArray: Array<String>): Boolean {
        var truthCheck = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                truthCheck = false
        }
        return truthCheck
    }

    private class CustomAdapter(context: Context, arrtitle: ArrayList<String>, arrmessage: ArrayList<String>, arrids: ArrayList<Int>): BaseAdapter() {

        val memoTitles = arrtitle
        val memoNotes = arrmessage
        val ids = arrmessage

        private val priContext: Context

        init {
            priContext = context
        }

        //Defines the number of notes shown
        override fun getCount(): Int {
            return memoTitles.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "TESTING"
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = LayoutInflater.from(priContext)
            val rowTest = inflater.inflate(R.layout.layout_test_row, parent, false)

            rowTest.findViewById<TextView>(R.id.remname_textview)

            val titleView = rowTest.findViewById<TextView>(R.id.remname_textview)
            titleView.text = memoTitles.get(position)

            val noteView = rowTest.findViewById<TextView>(R.id.reminder_textview)
            noteView.text = memoNotes.get(position)

            return rowTest
        }

    }
}