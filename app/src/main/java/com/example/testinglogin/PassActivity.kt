package com.example.testinglogin

import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import java.util.*
import java.util.concurrent.TimeUnit

class PassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val oldName = findViewById<EditText>(R.id.oldNameInput)
        val oldPass = findViewById<EditText>(R.id.oldPassInput)
        val newPass = findViewById<EditText>(R.id.newPassInput)
        val applyButton = findViewById<Button>(R.id.addButton)
        val context = this

        applyButton.setOnClickListener {
            // Here we retrieve the information from SharedPreferences
            val nameCheck = sharedPreferences.getString("trueName", "")
            val passCheck = sharedPreferences.getString("truePass", "")

            // Here we get the user input
            val nameTest: String = oldName.text.toString()
            val passTest: String = oldPass.text.toString()
            val newIn: String = newPass.text.toString()

            // Here we check the name and password
            if (passTest == passCheck && nameTest == nameCheck && newIn != "") {
                val intent = Intent(this, MainActivity::class.java)
                editor.putString("truePass", newIn)
                editor.apply()

                findViewById<EditText>(R.id.oldNameInput).getText().clear()
                findViewById<EditText>(R.id.oldPassInput).getText().clear()
                findViewById<EditText>(R.id.newPassInput).getText().clear()
                finish()
                startActivity(intent)
            } else {
                Toast.makeText(context, "Information is incorrect. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}