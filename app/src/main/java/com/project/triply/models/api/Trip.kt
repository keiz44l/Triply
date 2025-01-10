package com.project.triply.models.api

data class Trip(
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val price: Double,
)
