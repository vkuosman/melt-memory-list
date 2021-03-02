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
import android.system.Os.remove
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class ReminderWorker(context: Context, params: WorkerParameters): Worker(context, params) {
    // Here we get the reminder info
    val newTitle = sharedPreferences.getString("newTitle", "")
    val newNote = sharedPreferences.getString("newNote", "")

    // Notification Manager
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "com.example.testinglogin"
    private val description = newNote

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
        // Testing notification features.
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationManager.createNotificationChannel(notificationChannel)

        // Is the round icon displayed in the notification the app icon?
        builder = Notification.Builder(applicationContext, channelId)
            .setContentTitle(newTitle)
            .setContentText("Reminder")
            .setSmallIcon(R.drawable.ic_launcher_round_new)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_launcher))
            .setContentIntent(pendingIntent)

        var timerList = sharedPreferences.getString("timerList", "")
        var timerStr = sharedPreferences.getString("newTimerNum", "")
        var strs = timerList?.split(",")?.toTypedArray()
        var removInt = -1
        var counter = 0
        if (strs != null) {
            while (counter < strs.size) {
                if (strs[counter] == timerStr) {
                    removInt = counter
                }
                counter += 1
            }
        }
        val mutable = strs?.toMutableList()
        if (mutable != null) {
            mutable.removeAt(removInt)
            strs = mutable.toTypedArray()
        }
        var arrayStr = strs?.joinToString()
        Log.i("MYTAG","$strs")
        if (timerStr != null) {
            notificationManager.notify(timerStr.toInt(), builder.build())
        }
    }
}