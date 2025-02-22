package com.project.triply.utils


fun String.toMinutes(): Double {
    val parts = this.split("h", "m")
    val hours = parts[0].toDoubleOrNull() ?: 0.0
    val minutes = parts[1].replace("m", "").toDoubleOrNull() ?: 0.0
    return hours * 60 + minutes
}
