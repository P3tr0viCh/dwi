package ru.p3tr0vich.dwi

import android.content.Context
import android.text.format.DateUtils
import java.time.YearMonth
import java.util.*

fun dateTimeToString(context: Context, date: Long): String {
    return DateUtils.formatDateTime(
        context, date, DateUtils.FORMAT_SHOW_DATE
    )
}

fun dateTimeToString(context: Context, date: Date): String {
    return dateTimeToString(context, date.time)
}

fun dateOnly(date: Date) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = date.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    date.time = calendar.timeInMillis
}

data class DateDiff(
    val totalDays: Int = 0,
    val years: Int = 0,
    val months: Int = 0,
    val days: Int = 0,
)

fun daysDiff(date1: Date, date2: Date): DateDiff {
    val d1: Long
    val d2: Long

    dateOnly(date1)
    dateOnly(date2)

    if (date1 > date2) {
        d1 = date2.time
        d2 = date1.time
    } else {
        d1 = date1.time
        d2 = date2.time
    }

    val calendar = Calendar.getInstance()

    calendar.timeInMillis = d1
    val year1 = calendar.get(Calendar.YEAR)
    val month1 = calendar.get(Calendar.MONTH)
    val day1 = calendar.get(Calendar.DAY_OF_MONTH)

    calendar.timeInMillis = d2
    var year2 = calendar.get(Calendar.YEAR)
    var month2 = calendar.get(Calendar.MONTH)
    var day2 = calendar.get(Calendar.DAY_OF_MONTH)

    if (day2 < day1) {
        month2 -= 1

        if (month2 == -1) {
            month2 = 11
            year2 -= 1
        }

        day2 += YearMonth.of(year2, month2 + 1).lengthOfMonth()
    }

    if (month2 < month1) {
        month2 += 12
        year2 -= 1
    }

    return DateDiff(
        ((d2 - d1) / DateUtils.DAY_IN_MILLIS).toInt(),
        year2 - year1,
        month2 - month1,
        day2 - day1
    )
}