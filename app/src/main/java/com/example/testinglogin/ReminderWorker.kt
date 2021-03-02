package com.example.testinglogin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.system.Os.remove
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class ReminderWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    // Notification Manager
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    val channelId = "com.example.testinglogin"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {

        val newTitle = inputData.getString(InputActivity.KEY_TITLE)
        val newNote = inputData.getString(InputActivity.KEY_NOTE)
        val counter = inputData.getString(InputActivity.KEY_NUM)

        if (newTitle != null && newNote != null && counter != null) {
            sendNotification(newTitle, newNote, counter)
        }
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification(title: String, note: String, remCounter: String) {

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)

        // Testing notification features.
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationChannel = NotificationChannel(channelId, note, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)

        // Is the round icon displayed in the notification the app icon?
        builder = Notification.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText("Reminder")
            .setSmallIcon(R.drawable.ic_launcher_round_new)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_launcher))
            .setContentIntent(pendingIntent)

        // Here we let the MainActivity know that the reminder can now be displayed:
        var timerList = sharedPreferences.getString("timerList", "")
        var strs = timerList?.split(",")?.toTypedArray()
        var elementCheck = -1
        var counter = 0
        if (strs != null) {
             while (counter < strs.size) {
                if (strs[counter] == remCounter) {
                    Log.i("MYTAG","REM $remCounter")
                    elementCheck = counter
                }
                 counter += 1
            }
        }
        if (elementCheck >= 0) {
            if (strs != null) {
                val newArray = strs.toMutableList()
                newArray.removeAt(elementCheck)
                strs = newArray.toTypedArray()
            }
        }
        var arrayString = strs?.joinToString()
        if (arrayString != null) {
            arrayString = arrayString.replace("\\s".toRegex(), "")
        }
        Log.i("MYTAG","$arrayString")
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("timerList", arrayString)
        editor.apply()
        notificationManager.notify(remCounter.toInt(), builder.build())

    }
}