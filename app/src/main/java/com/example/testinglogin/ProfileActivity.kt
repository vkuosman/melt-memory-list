package com.example.testinglogin
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.concurrent.TimeUnit


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        val intentNot = Intent(this, InputActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intentNot,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Forgetting this line may cause major headaches
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        Log.i("MYTAG","000000")

        var timerList = sharedPreferences.getString("timerList", "")
        val strs = timerList?.split(",")?.toTypedArray()

        var db = DataBaseHandler(applicationContext)
        var data = db.readData()
        var titleArray = arrayListOf<String>()
        var messArray = arrayListOf<String>()
        var idArray = arrayListOf<Int>()

        Log.i("MYTAG","11111")

        var viewCount = 0
        var hideCount = 0
        for (i in 0..data.size - 1) {
            var checker = true
            if (strs != null) {
                for (s in strs) {
                    if (s == data.get(i).id) {
                        hideCount += 1
                        checker = false

                    }
                }
            }
            if (checker) {
                viewCount += 1
            }
        }

        Log.i("MYTAG","2222222")
        val nameCheck = sharedPreferences.getString("trueName", "")
        val profileName = findViewById<View>(R.id.nameText) as? TextView
        profileName?.setText(nameCheck)

        val viewsCounted = findViewById<View>(R.id.row1) as? TextView
        viewsCounted?.setText("The number of activated reminders: $viewCount")

        val hidesCounted = findViewById<View>(R.id.row2) as? TextView
        hidesCounted?.setText("The number of upcoming reminders: $hideCount")

        val context = this

        val passButton = findViewById<Button>(R.id.passButton)
        val nameButton = findViewById<Button>(R.id.nameButton)

        passButton.setOnClickListener {
            val intent = Intent(this, PassActivity::class.java)
            finish()
            startActivity(intent)
        }

        nameButton.setOnClickListener {
            val intent = Intent(this, NameActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}