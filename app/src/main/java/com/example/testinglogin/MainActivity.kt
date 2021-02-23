package com.example.testinglogin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

lateinit var sharedPreferences: SharedPreferences

// The current implementation is meant to demonstrate ListView with placeholder messages
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val memButton = findViewById<Button>(R.id.memoButtonMain)
        val editButton = findViewById<Button>(R.id.editButtonMain)
        val numInput = findViewById<EditText>(R.id.numInputMain)

        val listView = findViewById<ListView>(R.id.MemoryList)

        val context = this
        var db = DataBaseHandler(context)
        var data = db.readData()
        var titleArray = arrayListOf<String>()
        var messArray = arrayListOf<String>()
        var idArray = arrayListOf<Int>()
        for (i in 0..data.size - 1) {
            titleArray.add(data.get(i).name)
            messArray.add(data.get(i).mess)
        }

        listView.adapter = CustomAdapter(this, titleArray, messArray, idArray)

        memButton.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            finish()
            startActivity(intent)
        }

        editButton.setOnClickListener {
            val numIn: String = numInput.text.toString()
            if (numIn.isBlank() || numIn.isBlank()) {
                Toast.makeText(context, "Please give the number of the reminder you wish to edit.", Toast.LENGTH_SHORT).show()
            } else {
                val finalNumIn = numIn.toInt()
                sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("tempEditNum", finalNumIn)
                editor.apply()
                val intent = Intent(this, EditActivity::class.java)
                finish()
                startActivity(intent)
            }
        }
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