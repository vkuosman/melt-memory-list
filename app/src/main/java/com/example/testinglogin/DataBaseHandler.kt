package com.example.testinglogin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME = "SQLiteTest"
val TABLE_NAME = "Reminders"
val COL_TITLE = "Title"
val COL_MESS = "Message"
val COL_ID = "_id"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL_TITLE + " VARCHAR(256)," + COL_MESS + " VARCHAR(256))";
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Not currently implemented
    }

    fun insertData(rem: ReminderClass) {
        // THis function is used to insert data into the database.
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_TITLE, rem.name)
        cv.put(COL_MESS, rem.mess)
        cv.put(COL_ID, rem.id)
        var result = db.insert(TABLE_NAME, null, cv)
        if (result == -1.toLong()) {

            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()

        } else {

            Toast.makeText(context, "Reminder added", Toast.LENGTH_LONG).show()

        }
    }
    fun readData() : MutableList<ReminderClass>{
        // This function is used to read data from the database.
        var list : MutableList<ReminderClass> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_NAME
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {

            do {

                var rem = ReminderClass()
                rem.id = result.getString(result.getColumnIndex(COL_ID))
                rem.name = result.getString(result.getColumnIndex(COL_TITLE))
                rem.mess = result.getString(result.getColumnIndex(COL_MESS))
                list.add(rem)

            } while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun deleteData(idIn: String) : MutableList<ReminderClass>{
        // This function is used to delete existing entries in the database.
        var list : MutableList<ReminderClass> = ArrayList()
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COL_ID+"=?", arrayOf(idIn))
        return list
        db.close()
    }

    fun editData(titleIn: String, messIn: String, idIn: String) : MutableList<ReminderClass>{
        // This function is used to edit existing data.
        var list : MutableList<ReminderClass> = ArrayList()
        val db = this.writableDatabase
        val cv = ContentValues()
        val titleFinal = idIn + ": " + titleIn
        cv.put(COL_TITLE, titleFinal)
        cv.put(COL_MESS, messIn)
        cv.put(COL_ID, idIn)
        db.update("Reminders", cv, "_id = ?", arrayOf(idIn))
        return list
        db.close()
    }

}