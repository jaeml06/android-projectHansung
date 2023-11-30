package com.example.hansungmarket.control.utils

import android.text.format.DateUtils
import java.text.ParseException
import java.util.Date

fun Date.timeAgoString(): String {
    try {
        val cur = System.currentTimeMillis()
        val prv = DateUtils.getRelativeTimeSpanString(time, cur, DateUtils.MINUTE_IN_MILLIS)
        return prv.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    throw IllegalStateException()
}