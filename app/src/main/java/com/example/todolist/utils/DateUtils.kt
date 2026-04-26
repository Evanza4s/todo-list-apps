package com.example.todolist.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatDueDate(dueDateMillis: Long): String {
    if (dueDateMillis == 0L) return "Waktu belum diatur"

    val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_YEAR)
    val currentYear = calendar.get(Calendar.YEAR)

    calendar.timeInMillis = dueDateMillis
    val dueDay = calendar.get(Calendar.DAY_OF_YEAR)
    val dueYear = calendar.get(Calendar.YEAR)

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    return when {
        dueYear == currentYear && dueDay == today -> "Hari ini, ${timeFormat.format(calendar.time)}"
        dueYear == currentYear && dueDay == today + 1 -> "Besok, ${timeFormat.format(calendar.time)}"
        else -> dateFormat.format(calendar.time)
    }
}

fun formatExactDueDate(dueDateMillis: Long): String {
    if (dueDateMillis == 0L) return "Waktu belum diatur"

    val calendar = Calendar.getInstance().apply {
        timeInMillis = dueDateMillis
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return dateFormat.format(calendar.time)
}
