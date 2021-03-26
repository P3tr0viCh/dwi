package ru.p3tr0vich.dwi

import android.app.Activity
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.p3tr0vich.dwi.databinding.ActivitySettingsBinding
import java.util.*

class ActivitySettings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, FragmentSettings())
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "0.0"
        }

        binding.textAppVersion.text = getString(R.string.about_version, versionName)
        binding.textAppBuildDate.text = dateTimeToString(this, BuildConfig.BUILD_DATE)
    }

    class FragmentSettings : PreferenceFragmentCompat(),
        Preference.OnPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            with(findPreference<Preference>(getString(R.string.pref_datetime_key))!!) {
                summaryProvider = DatePreference.SimpleSummaryProvider.instance
                onPreferenceChangeListener = this@FragmentSettings
            }
            findPreference<Preference>(getString(R.string.pref_app_theme_key))!!.onPreferenceChangeListener =
                this
        }

        override fun onDisplayPreferenceDialog(preference: Preference?) {
            if (preference is DatePreference) {
                val calendar = Calendar.getInstance()

                if (preference.date != null) {
                    calendar.timeInMillis = preference.date!!
                }

                DatePickerDialog(
                    requireActivity(),
                    { _, year, monthOfYear, dayOfMonth ->
                        calendar.set(year, monthOfYear, dayOfMonth)

                        preference.date = calendar.timeInMillis
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                    .show()
            } else {
                super.onDisplayPreferenceDialog(preference)
            }
        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            return when (preference.key) {
                getString(R.string.pref_datetime_key) -> {
                    requireActivity().setResult(Activity.RESULT_OK)
                    true
                }
                getString(R.string.pref_app_theme_key) -> {
                    when (newValue.toString()) {
                        getString(R.string.pref_app_theme_night_no_value) ->
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        getString(R.string.pref_app_theme_night_yes_value) ->
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else ->
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    true
                }
                else -> false
            }
        }
    }
}