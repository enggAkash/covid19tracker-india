package `in`.engineerakash.covid19india.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    val APPS_DEFAULT_DATE_TIME_FORMAT = "dd MMM, yyyy, hh:mm a"
    val APPS_DEFAULT_DATE_TIME_FORMAT_SHORT = "d MMM, yy, h:mm a"
    val APPS_DEFAULT_DATE_FORMAT = "dd MMM, yyyy"
    val APPS_DEFAULT_DATE_FORMAT_SHORT = "dd MMM, yy"
    val APPS_DEFAULT_TIME_FORMAT = "hh:mm a"
    val DATE_FILTER_FORMAT = "yyyy-MMM-dd"

    val todayCalendar = Calendar.getInstance()

    fun getCurrentTimeZoneFormat(timeFormat: String?): SimpleDateFormat {
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        val currentDate = Date()
        val tz = Calendar.getInstance().timeZone

        //String name1 = tz.getDisplayName(tz.inDaylightTime(currentDate), TimeZone.SHORT);
        val name =
            TimeZone.getDefault().getDisplayName(tz.inDaylightTime(currentDate), TimeZone.SHORT)
        sdf.timeZone = TimeZone.getTimeZone("\"" + name + "\"")
        //AppLog.d("current time zone", sdf.getTimeZone().getDisplayName() + "::" + TimeZone.getDefault().getDisplayName() + ": " + TimeZone.getDefault().getID());
        return sdf
    }


    fun getTime(timeInMilliSeconds: Long, timeFormat: String?): String {
        val sdf = SimpleDateFormat(timeFormat)
        val calendar = Calendar.getInstance()
        if (timeInMilliSeconds > 0) calendar.timeInMillis = timeInMilliSeconds
        return sdf.format(calendar.time)
    }

    fun convertDateFormatNoZoneConversion(
        dateString: String?,
        oldFormat: String?,
        newFormat: String?
    ): String {
        val originalFormat: DateFormat = SimpleDateFormat(oldFormat)
        val targetFormat: DateFormat = SimpleDateFormat(newFormat)
        var date: Date? = null
        try {
            date = originalFormat.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return targetFormat.format(date)
    }


    fun getDate(
        timeInMilliSeconds: Long,
        outputTimeFormat: String?,
        convertToLocalTime: Boolean
    ): Date {
        val sdf =
            if (convertToLocalTime) getCurrentTimeZoneFormat(outputTimeFormat) else SimpleDateFormat(
                outputTimeFormat
            )
        val calendar = Calendar.getInstance()
        if (timeInMilliSeconds > 0) calendar.timeInMillis = timeInMilliSeconds
        return try {
            sdf.parse(sdf.format(calendar.time))
        } catch (e: Exception) {
            e.printStackTrace()
            Date()
        }
    }

    fun getDate(timeInMilliSeconds: Long, outputTimeFormat: String?): Date {
        return getDate(timeInMilliSeconds, outputTimeFormat, true)
    }

    fun getDate(time: String?, inputTimeFormat: String?, outputTimeFormat: String?): Date {
        val sdf = getCurrentTimeZoneFormat(inputTimeFormat)
        val sdfo = SimpleDateFormat(outputTimeFormat)
        val calendar = Calendar.getInstance()
        try {
            calendar.time = sdf.parse(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return try {
            sdfo.parse(sdfo.format(calendar.time))
        } catch (e: Exception) {
            e.printStackTrace()
            Date()
        }
    }

    @Throws(ParseException::class)
    fun getOnlyDate(date: String, format: String?): Date {
        val date1 = SimpleDateFormat(format).parse(date)
        println(date + "\t" + date1)
        return date1
    }


    /**
     * @return Input Date in APPS Default Date Format: 12 May, 2019, 11:21 AM
     */
    fun parseDateTimeToAppsDefaultDateTimeFormat(
        inputDateTime: String?,
        inputDateFormatString: String = "yyyy-MM-dd HH:mm:ss"
    ): String {
        var outputDateTime = inputDateTime
        val inputDateTimeFormat = SimpleDateFormat(inputDateFormatString, Locale.ENGLISH)
        val outputDateTimeFormat = SimpleDateFormat(APPS_DEFAULT_DATE_TIME_FORMAT, Locale.ENGLISH)
        try {
            val inputDateTimeParsed = inputDateTimeFormat.parse(inputDateTime.toString())
            if (inputDateTimeParsed != null) outputDateTime =
                outputDateTimeFormat.format(inputDateTimeParsed)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDateTime.toString()
    }

    /**
     * @return Input Date in APPS Default Date Format: 12 May, 2019
     */
    fun parseDateTimeToAppsDefaultDateFormat(
        inputDateTime: String?,
        inputDateFormatString: String = "yyyy-MM-dd HH:mm:ss"
    ): String {
        var outputDateTime = inputDateTime
        val inputDateTimeFormat = SimpleDateFormat(inputDateFormatString, Locale.ENGLISH)
        val outputDateTimeFormat = SimpleDateFormat(APPS_DEFAULT_DATE_FORMAT, Locale.ENGLISH)
        try {
            val inputDateTimeParsed = inputDateTimeFormat.parse(inputDateTime.toString())
            if (inputDateTimeParsed != null) outputDateTime =
                outputDateTimeFormat.format(inputDateTimeParsed)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDateTime.toString()
    }

    /**
     * @return Input Date in APPS Default Time Format: 11:21 AM
     */
    fun parseDateTimeToAppsDefaultTimeFormat(
        inputDateTime: String?,
        inputDateFormatString: String = "yyyy-MM-dd HH:mm:ss"
    ): String {
        var outputDateTime = inputDateTime
        val inputDateTimeFormat = SimpleDateFormat(inputDateFormatString, Locale.ENGLISH)
        val outputDateTimeFormat = SimpleDateFormat(APPS_DEFAULT_TIME_FORMAT, Locale.ENGLISH)
        try {
            val inputDateTimeParsed = inputDateTimeFormat.parse(inputDateTime.toString())
            if (inputDateTimeParsed != null) outputDateTime =
                outputDateTimeFormat.format(inputDateTimeParsed)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDateTime.toString()
    }

    fun parseDateTimeToCustomDateTimeFormat(
        inputDateTime: String?,
        inputDateTimeFormatString: String = "yyyy-MM-dd HH:mm:ss",
        outputDateTimeFormatString: String = APPS_DEFAULT_DATE_TIME_FORMAT
    ): String {
        var outputDateTime = inputDateTime
        val inputDateTimeFormat = SimpleDateFormat(inputDateTimeFormatString, Locale.ENGLISH)
        val outputDateTimeFormat = SimpleDateFormat(outputDateTimeFormatString, Locale.ENGLISH)
        try {
            val inputDateTimeParsed = inputDateTimeFormat.parse(inputDateTime.toString())
            if (inputDateTimeParsed != null) outputDateTime =
                outputDateTimeFormat.format(inputDateTimeParsed)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDateTime.toString()
    }

    fun parseDashboardDateToCalendar(inputDate: String?): Calendar {
        val calendar = Calendar.getInstance()
        val inputDateFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH)
        try {
            val inputDateParsed = inputDateFormat.parse(inputDate)
            if (inputDateParsed != null) calendar.time = inputDateParsed
        } catch (e: ParseException) {
            //e.printStackTrace()
        }
        return calendar
    }

    // [0]=start_date, [1]=end_date, Output Format: 2019-Nov-01
    fun getSelectedFilterDate(position: Int): ArrayList<String> {
        val startAndEndDate = arrayListOf<String>()

        val outputFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH)

        when (position) {
            0 -> {
                // Today

                val todayCal = Calendar.getInstance()
                val formatted = outputFormat.format(todayCal.time)

                startAndEndDate.add(formatted)
                startAndEndDate.add(formatted)

            }
            1 -> {
                // Yesterday
                val yesterdayCal = Calendar.getInstance()
                yesterdayCal.add(Calendar.DATE, -1)
                val formatted = outputFormat.format(yesterdayCal.time)

                startAndEndDate.add(formatted)

                val today = Calendar.getInstance()
                startAndEndDate.add(outputFormat.format(today.time))

            }
            2 -> {
                // Last 30 Days
                val previousMonthCal = Calendar.getInstance()
                previousMonthCal.add(Calendar.DATE, -30)
                startAndEndDate.add(outputFormat.format(previousMonthCal.time))


                val today = Calendar.getInstance()
                startAndEndDate.add(outputFormat.format(today.time))


            }
            3 -> {
                // All
                val previousDecadeCal = Calendar.getInstance()
                previousDecadeCal.add(Calendar.YEAR, -10)
                startAndEndDate.add(outputFormat.format(previousDecadeCal.time))


                val today = Calendar.getInstance()
                startAndEndDate.add(outputFormat.format(today.time))
            }
        }

        return startAndEndDate
    }

    fun String?.convertToDateLong(
        dateFormat: String = "dd MMM yyyy",
        timezone: String = "DEFAULT"
    ): Long {
        var milliseconds: Long? = 0L
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())

        if (timezone != "DEFAULT")
            sdf.timeZone = TimeZone.getTimeZone(timezone)

        try {
            val d = sdf.parse(this.toString())
            milliseconds = d?.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        if (milliseconds == null || milliseconds == 0L)
            milliseconds = Date().time

        return milliseconds
    }

    const val DEFAULT_START_DATE = 946665000000 //  January 1, 2000
    fun currentDateToLong(): Long {
        return Date().time
    }

    /**
     * return:
    the value 0 if the argument Date is equal to this Date; a value less than 0 if this Date is before the Date argument;
    and a value greater than 0 if this Date is after the Date argument.
     */
    fun String?.compareDateTo(anotherDate: String?, dateFormat: String = "dd MMM yyyy"): Int {
        var compareTo = 0
        if (anotherDate.isNullOrEmpty())
            compareTo = 1
        else if (this.isNullOrEmpty())
            compareTo = -1
        else {

            try {
                val thisDateSDF = SimpleDateFormat(dateFormat, Locale.getDefault())
                val anotherDateSDF = SimpleDateFormat(dateFormat, Locale.getDefault())

                compareTo =
                    thisDateSDF.parse(this.toString()).compareTo(anotherDateSDF.parse(anotherDate))
            } catch (e: java.lang.Exception) {

            }
        }
        return compareTo
    }

    fun getTotalDurationString(duration: Int): String {
        if ((duration) > 0) {
            // in ms
            val totalDuration = (duration) / 1000
            val minutes = (totalDuration) / 60
            val seconds = (totalDuration) % 60
            if ((minutes / 60) >= 10) {
                if (seconds >= 10) {
                    return "${minutes}:${seconds}"
                } else {
                    return "${minutes}:0${seconds}"
                }
            } else {
                if (seconds >= 10) {
                    return "0${minutes}:${seconds}"
                } else {
                    return "0${minutes}:0${seconds}"
                }
            }
        } else {
            return "00:00"
        }
    }

    fun getTotalDurationStringWithMinuteAndSecond(durationInSecond: Long): String {
        if ((durationInSecond) > 0) {
            // in ms
            val minutes = (durationInSecond) / 60
            val seconds = (durationInSecond) % 60
            if ((minutes / 60) >= 10) {
                if (seconds >= 10) {
                    return "${minutes}m ${seconds}s"
                } else {
                    return "${minutes}m 0${seconds}s"
                }
            } else {
                if (seconds >= 10) {
                    return "0${minutes}m ${seconds}s"
                } else {
                    return "0${minutes}m 0${seconds}s"
                }
            }
        } else {
            return "NA"
        }
    }

    /**
     * input 2021-09-05T17:56:52+05:30
     * output d MMM, yy, h:mm a
     */
    fun parseMetaDateTimeToAppsDefaultDateTime(inputDateTime: String?, outputFormat: String = APPS_DEFAULT_DATE_TIME_FORMAT_SHORT): String {
        var outputDateTime = inputDateTime?.replace("T", " ")?.replace("+05:30", "")
        val inputDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("en", "IN"))
        val outputDateTimeFormat =
            SimpleDateFormat(outputFormat, Locale.getDefault())
        try {
            val inputDateTimeParsed = inputDateTimeFormat.parse(outputDateTime.toString())

            if (inputDateTimeParsed != null)
                outputDateTime = outputDateTimeFormat.format(inputDateTimeParsed)

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDateTime.toString()
    }
}