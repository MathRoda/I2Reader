package dev.mathroda.twelvereader.utils

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.takeWordsUpTo(number: Int): String {
    return split(" ").take(number).joinToString(" ")
}

@SuppressLint("DefaultLocale")
fun Long.toTime(): String {
    val stringBuffer = StringBuffer()

    val minutes = (this / 60000).toInt()
    val seconds = (this % 60000 / 1000).toInt()

    stringBuffer
        .append(String.format("%02d", minutes))
        .append(":")
        .append(String.format("%02d", seconds))

    return stringBuffer.toString()
}

fun LocalDate.toDisplay(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
    return this.format(formatter)
}