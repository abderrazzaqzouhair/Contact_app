package com.app.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


lateinit var context: Context
class SettingsActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        context = this

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        lateinit var myPreference: MyPreference


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val listPreference: ListPreference? = findPreference("reply")
            myPreference = MyPreference(requireContext())
            listPreference?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val selectedValue = newValue as String
                    when (selectedValue) {
                        getString(R.string.english) -> {
                            myPreference.setLoginCount("en")
                            startActivity(Intent(context,MainActivity::class.java))
                        }

                         getString(R.string.arabic)-> {
                            myPreference.setLoginCount("ar")
                            startActivity(Intent(context,MainActivity::class.java))
                        }

                        getString(R.string.french) -> {
                            myPreference.setLoginCount("fr")
                            startActivity(Intent(context,MainActivity::class.java))
                        }
                        else -> {
                        }
                    }
                    true
                }
        }
    }
}


