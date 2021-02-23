package com.example.testinglogin


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        // For the sake of demonstration, SharedPreferences and the database are cleared.
        // Feel free to comment this out if you wish to do further testing.
        editor.clear()
        editor.commit()
        val context = this
        context.deleteDatabase("SQLiteTest");
        editor.putInt("memoCounter", 1)
        editor.apply()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val logButton = findViewById<Button>(R.id.LogInButton)
        val signButton = findViewById<Button>(R.id.SignUpButton)
        val nameIn = findViewById<EditText>(R.id.NameInput)
        val passIn = findViewById<EditText>(R.id.PasswordInput)

        // Testing Sign Up functions
        signButton.setOnClickListener {

            // Here we retrieve the information from SharedPreferences
            val nameCheck = sharedPreferences.getString("trueName", "")
            val passCheck = sharedPreferences.getString("truePass", "")

            // Here we get the user input
            val nameIn: String = nameIn.text.toString()
            val passIn: String = passIn.text.toString()

            // Here we check the name and password
            if (nameCheck.isNullOrEmpty() || nameCheck.isBlank()) {
                if (nameIn.isBlank() || passIn.isBlank()) {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                }
                else {
                    editor.putString("trueName", nameIn)
                    editor.putString("truePass", passIn)
                    editor.apply()

                    val intent = Intent(this, MainActivity::class.java)
                    findViewById<EditText>(R.id.NameInput).getText().clear()
                    findViewById<EditText>(R.id.PasswordInput).getText().clear()
                    startActivity(intent)
                }
            } else {
                Toast.makeText(context, "There already is an account.", Toast.LENGTH_SHORT).show()
            }
        }

        // Testing Login functions
        logButton.setOnClickListener {

            // Here we retrieve the information from SharedPreferences
            val nameCheck = sharedPreferences.getString("trueName", "")
            val passCheck = sharedPreferences.getString("truePass", "")

            // Here we get the user input
            val nameTest: String = nameIn.text.toString()
            val passTest: String = passIn.text.toString()

            // Here we check the name and password
            if (passTest == passCheck && nameTest == nameCheck && passTest != "" && nameTest != "") {
                val intent = Intent(this, MainActivity::class.java)

                findViewById<EditText>(R.id.NameInput).getText().clear()
                findViewById<EditText>(R.id.PasswordInput).getText().clear()
                startActivity(intent)
            } else {
                Toast.makeText(context, "Information is incorrect or the account does not exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}