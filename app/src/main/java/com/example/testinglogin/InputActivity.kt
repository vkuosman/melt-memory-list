package com.example.testinglogin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        // Forgetting this line may cause major headaches
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val titleIn = findViewById<EditText>(R.id.titleInput)
        val noteIn = findViewById<EditText>(R.id.noteInput)
        val addButton = findViewById<Button>(R.id.addButton)
        val context = this

        addButton.setOnClickListener {

            editor.commit()
            // Here we get the user input
            var titleTest: String = titleIn.text.toString()
            val noteTest: String = noteIn.text.toString()

            if (titleTest.length == 0 || noteTest.length == 0) {
                Toast.makeText(context, "Please give the title and the reminder message.", Toast.LENGTH_SHORT).show()
            } else {
                val memoCounter = sharedPreferences.getInt("memoCounter", 0)
                val counterStr = memoCounter.toString()
                titleTest = counterStr + ": " + titleTest
                var db = DataBaseHandler(context)
                var rem = ReminderClass(titleTest, noteTest, counterStr)
                db.insertData(rem)
                editor.putInt("memoCounter", memoCounter + 1)
                editor.apply()

                findViewById<EditText>(R.id.titleInput).getText().clear()
                findViewById<EditText>(R.id.noteInput).getText().clear()

                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            }
        }
    }
}

