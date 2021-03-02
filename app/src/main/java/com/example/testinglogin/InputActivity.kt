package com.example.testinglogin
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit

class InputActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        val intentNot = Intent(this, InputActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intentNot, PendingIntent.FLAG_UPDATE_CURRENT)

        // Forgetting this line may cause major headaches
        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val titleIn = findViewById<EditText>(R.id.titleInput)
        val noteIn = findViewById<EditText>(R.id.noteInput)
        val addButton = findViewById<Button>(R.id.addButton)
        val timeButton: Button = findViewById<Button>(R.id.timeButton)
        val dateButton: Button = findViewById<Button>(R.id.dateButton)
        val context = this

        dateButton.setOnClickListener {
            getCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }

        timeButton.setOnClickListener {
            getCalendar()
            TimePickerDialog(this, this, hour, minute, true).show()
        }

        addButton.setOnClickListener {

            // Here we get the user input
            var titleTest: String = titleIn.text.toString()
            val noteTest: String = noteIn.text.toString()

            // Here we check that the necessary information has been given
            if (titleTest.length == 0 || noteTest.length == 0) {

                Toast.makeText(context, "Please give the title and the reminder message.", Toast.LENGTH_SHORT).show()

            } else {

                val memoCounter = sharedPreferences.getInt("memoCounter", 0)
                val counterStr = memoCounter.toString()

                titleTest = counterStr + ": " + titleTest

                var db = DataBaseHandler(context)
                var rem = ReminderClass(titleTest, noteTest, counterStr)

                // Here we insert the data to the database
                db.insertData(rem)
                editor.putInt("memoCounter", memoCounter + 1)
                editor.apply()

                // Calculating timer
                var curDate = Calendar.getInstance()
                var newDate = curDate.clone() as Calendar
                newDate.set(savedYear, savedMonth - 1, savedDay, savedHour, savedMinute)
                var newDateTime = newDate.time
                var curDateTime = curDate.time

                var diff: Long = newDateTime.getTime() - curDateTime.getTime()
                var diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);

                // Debug
                Log.i("MYTAG", "Given year: $savedYear, Given month: $savedMonth, Given day: $savedDay, Given hour: $savedHour, Given minutes: $savedMinute, Current date: $curDateTime, New date: $newDateTime, Difference in minutes: $diffMinutes")

                // Giving the timer number to the Worker
                editor.putString("newTimerNum", counterStr)
                editor.apply()

                // Here we set up the notification
                setOneTimeNotification(titleTest, noteTest, counterStr, diffMinutes)

                // Here we reset the EditTexts. The timing could be improved.
                findViewById<EditText>(R.id.titleInput).getText().clear()
                findViewById<EditText>(R.id.noteInput).getText().clear()

                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            }
        }


    }

    private fun getCalendar() {
        val cal: Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month + 1
        savedYear = year

        getCalendar()
        TimePickerDialog(this, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
    }

    private fun setOneTimeNotification(title: String, note: String, counter: String, minutes: Long) {
        val notificationRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
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

        //var testArray = arrayOf("1", "2", "3")
        //var arrayStr = testArray.joinToString()
        //arrayStr = arrayStr.replace("\\s".toRegex(), "")
        //val strs = arrayStr.split(",").toTypedArray()
        //var arrayStr2 = strs.joinToString()
        //arrayStr2 = arrayStr2.replace("\\s".toRegex(), "")

        // Debug
        //Log.i("MYTAG","TIMERLIST: $timerList")
        //Log.i("MYTAG","STR TEST: $arrayStr")
        //Log.i("MYTAG","Back to array: $arrayStr2")

        editor.putString("newTitle", title)
        editor.putString("newNote", note)
        editor.apply()
    }
}


