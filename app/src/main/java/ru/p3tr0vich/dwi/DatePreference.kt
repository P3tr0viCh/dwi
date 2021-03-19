package ru.p3tr0vich.dwi

import android.content.Context
import android.content.res.TypedArray
import android.text.format.DateUtils
import android.util.AttributeSet
import androidx.preference.DialogPreference
import java.util.*

class DatePreference(context: Context?, attrs: AttributeSet) : DialogPreference(context, attrs) {

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        return a.getInt(index, 0)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        date = if (defaultValue is Long) getPersistedLong(defaultValue) else getPersistedLong(0)
    }

    var date: Long? = null
        set(value) {
            val wasBlocking = shouldDisableDependents()

            if (value != null) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = value
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }

            field = value

            persistLong(field ?: 0)

            val isBlocking = shouldDisableDependents()
            if (isBlocking != wasBlocking) {
                notifyDependencyChange(isBlocking)
            }

            notifyChanged()

            callChangeListener(field)
        }

    class SimpleSummaryProvider private constructor() : SummaryProvider<DatePreference> {
        override fun provideSummary(preference: DatePreference): CharSequence {
            return if (preference.date == null) {
                preference.context.getString(R.string.not_set)
            } else {
                DateUtils.formatDateTime(
                    preference.context, preference.date!!, DateUtils.FORMAT_SHOW_DATE
                )
            }
        }

        companion object {
            private var sSimpleSummaryProvider: SimpleSummaryProvider? = null

            val instance: SimpleSummaryProvider?
                get() {
                    if (sSimpleSummaryProvider == null) {
                        sSimpleSummaryProvider = SimpleSummaryProvider()
                    }
                    return sSimpleSummaryProvider
                }
        }
    }
}