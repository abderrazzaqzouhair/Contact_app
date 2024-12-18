package com.app.contacts

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {
    lateinit var myPreference: MyPreference

    private lateinit var recyclerView: RecyclerView
    private lateinit var callLogAdapter: CallLogAdapter
    private lateinit var noContact: LinearLayout
    private val callLogs = mutableListOf<CallLogItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(1400)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolBar))
        recyclerView = findViewById(R.id.recyclerView)
        noContact = findViewById(R.id.noContact)
        recyclerView.setOnClickListener {
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE), 1)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                noContact.visibility = View.GONE
                loadCallLogs()
            }else{
                noContact.visibility = View.VISIBLE
                findViewById<TextView>(R.id.noContactText).text = "Permission denied. Please click the box to grant access."
                findViewById<ImageView>(R.id.noContactImage).setOnClickListener {
                    ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.CALL_PHONE), 1)
                }

            }
        } else {
            noContact.visibility = View.GONE
            loadCallLogs()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        myPreference = MyPreference(newBase!!)
        val lang = myPreference.getLoginCount()
        super.attachBaseContext(MyContextWrapper.wrap(newBase,lang!!))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                callLogAdapter.filter(newText ?: "")
                return true
            }
        })

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }

            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            noContact.visibility = View.GONE
            loadCallLogs()
        }
    }

    private fun loadCallLogs() {
        val resolver = contentResolver
        val cursor = resolver.query(
            CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
            val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex) ?: "Unknown"
                val phoneNumber = cursor.getString(numberIndex) ?: "Unknown"
                val type = cursor.getInt(typeIndex)
                val date = Date(cursor.getLong(dateIndex))

                val callType = when (type) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }

                val dateFormatted =
                    SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(date)

                callLogs.add(CallLogItem(name, phoneNumber, callType, dateFormatted))
            }
        }
        callLogAdapter = CallLogAdapter(callLogs, this)
        recyclerView.adapter = callLogAdapter
    }

}





