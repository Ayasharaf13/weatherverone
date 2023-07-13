package com.example.weathermate.utilities

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("convertToDate")
fun convertToDate(view : TextView , timestamp: Long) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = Date(timestamp * 1000L)
    sdf.timeZone = TimeZone.getDefault()
    view.text = sdf.format(date)
}

@BindingAdapter("convertToTime")
fun convertToTime(view : TextView ,timestamp: Long) {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val date = Date(timestamp * 1000L)
    sdf.timeZone = TimeZone.getDefault()
    view.text = sdf.format(date)
}


fun getDateToAlert(timestamp: Long, language: String): String{
    return SimpleDateFormat("dd MMM, yyyy",Locale(language)).format(timestamp)
}
fun getTimeToAlert(timestamp: Long, language: String): String{
    return SimpleDateFormat("h:mm a",Locale(language)).format(timestamp)
}
fun convertDateToLong(date:String): Long {
    val format=SimpleDateFormat("dd MMM, yyyy")
    return format.parse(date).time
}
fun convertTimeToLong(time:String):Long{
    val format = SimpleDateFormat("hh:mm a")
    return format.parse(time).time
}