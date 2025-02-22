package com.project.triply.models.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val price: String,
    val type: String,
    val bookingLink: String,
    val departureStation: String = "Unknown", // New field for departure station
    val arrivalStation: String = "Unknown", // New field for arrival station
    val stops: String = "None" // New field for stops
) : Parcelable
