package com.example.testinglogin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class EditActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        val context = this
        var db = DataBaseHandler(context)
        val titleIn = findViewById<EditText>(R.id.oldNameInput)
        val noteIn = findViewById<EditText>(R.id.oldPassInput)
        val removeButton = findViewById<Button>(R.id.removeButton)
        val editButton = findViewById<Button>(R.id.addButton)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        removeButton.setOnClickListener {
            var db = DataBaseHandler(context)
            val memoCounter = sharedPreferences.getInt("tempEditNum", 0)
            val memoString = memoCounter.toString()
            db.deleteData(memoString)
            findViewById<EditText>(R.id.oldNameInput).getText().clear()
            findViewById<EditText>(R.id.oldPassInput).getText().clear()

            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }

        editButton.setOnClickListener {
            val memoCounter = sharedPreferences.getInt("tempEditNum", 0)
            val memoString = memoCounter.toString()
            var db = DataBaseHandler(context)

            // Here we get the user input
            var titleTest: String = titleIn.text.toString()
            val noteTest: String = noteIn.text.toString()

            if (titleTest != "" && noteTest != "") {
                db.editData(titleTest, noteTest, memoString)

                findViewById<EditText>(R.id.oldNameInput).getText().clear()
                findViewById<EditText>(R.id.oldPassInput).getText().clear()

                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            } else {

                Toast.makeText(context, "Please give the new title and message.", Toast.LENGTH_SHORT).show()

            }
        }
    }
}