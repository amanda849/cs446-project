package cs486.splash.shared

import java.util.Calendar
import java.util.Date

fun getDayMidnight(date : Date) : Date {
    val time: Long = date.time
    return Date(time - time % (24 * 60 * 60 * 1000))
}

fun getHour(date : Date) : Int {
    val cal : Calendar = Calendar.getInstance()
    cal.time = date

    return cal.get(Calendar.HOUR_OF_DAY)
}

fun getPastWeekBeginDate(endDate : Date) : Date {
    val cal : Calendar = Calendar.getInstance()
    cal.time = getDayMidnight(endDate)

    cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-6);

    return cal.time
}

fun getPastMonthBeginDate(endDate : Date) : Date {
    val cal : Calendar = Calendar.getInstance()
    cal.time = getDayMidnight(endDate)

    cal.add(Calendar.MONTH, -1);

    return cal.time
}

fun getPastYearBeginDate(endDate : Date) : Date {
    val cal : Calendar = Calendar.getInstance()
    cal.time = getDayMidnight(endDate)

    cal.add(Calendar.YEAR, -1);

    return cal.time
}
