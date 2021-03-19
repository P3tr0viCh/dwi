package ru.p3tr0vich.dwi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import ru.p3tr0vich.dwi.databinding.ActivityMainBinding
import java.util.*

class ActivityMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var dateTime: Date = Calendar.getInstance().time
        set(value) {
            field = value

            binding.textDatetime.text = dateTimeToString(applicationContext, field)

            updateCounters()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnPrefs.setOnClickListener {
            startForResult.launch(Intent(this, ActivitySettings::class.java))
        }

//        PreferenceManager.getDefaultSharedPreferences(this).edit().remove(getString(R.string.pref_datetime_key)).apply()

        dateTime = if (savedInstanceState == null) {
            Date(
                PreferenceManager.getDefaultSharedPreferences(this)
                    .getLong(getString(R.string.pref_datetime_key), dateTime.time)
            )
        } else {
            Date(savedInstanceState.getLong(DATETIME))
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                dateTime = Date(
                    PreferenceManager.getDefaultSharedPreferences(this)
                        .getLong(getString(R.string.pref_datetime_key), dateTime.time)
                )
            }

        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(DATETIME, dateTime.time)
    }

    private fun updateCounters() {
        with(daysDiff(Calendar.getInstance().time, dateTime)) {
            with(binding) {
                textTotalDays.text = totalDays.toString()
                textTotalDaysLabel.text = resources.getQuantityString(R.plurals.days, totalDays)

                textYears.text = years.toString()
                textYearsLabel.text = resources.getQuantityString(R.plurals.years, years)

                textYears.visibility = if (years > 0) View.VISIBLE else View.GONE
                textYearsLabel.visibility = textYears.visibility

                textMonths.text = months.toString()
                textMonthsLabel.text = resources.getQuantityString(R.plurals.months, months)

                textMonths.visibility = if (months > 0) View.VISIBLE else View.GONE
                textMonthsLabel.visibility = textMonths.visibility

                textDays.text = days.toString()
                textDaysLabel.text = resources.getQuantityString(R.plurals.days, days)

                textDays.visibility = if (days in 1 until totalDays) View.VISIBLE else View.GONE
                textDaysLabel.visibility = textDays.visibility
            }
        }
    }

    companion object {
        private const val DATETIME = "DATETIME"
    }
}