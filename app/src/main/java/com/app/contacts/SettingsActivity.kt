package com.app.contacts

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val listPreference: ListPreference? = findPreference("reply")

            listPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val selectedValue = newValue as String
                Log.d("SettingsFragment", "$selectedValue")
                when (selectedValue) {
                    "english" -> {
                        changeLanguage(requireContext(), "en")
                    }
                    "arabic" -> {
                        changeLanguage(requireContext(), "ar")
                    }
                    "french" -> {
                        changeLanguage(requireContext(), "fr")
                    }
                    else -> {
                    }
                }
                true
            }
        }
    }}
    fun changeLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale) // For API level 17 and above
        } else {
            config.locale = locale // For API level below 17
        }

}