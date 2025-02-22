package com.project.triply.models.api

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NavitiaResponse(
    val journeys: List<Journey>
)

data class Journey(
    val duration: Int,
    val departure_date_time: String,
    val arrival_date_time: String,
    val sections: List<Section>,
    val co2_emission: Co2Emission,
    val fare: Fare?
) {
    val departureTime: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
            val dateTime = LocalDateTime.parse(departure_date_time, formatter)
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

    val arrivalTime: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
            val dateTime = LocalDateTime.parse(arrival_date_time, formatter)
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

    val formattedDuration: String
        get() {
            val totalMinutes = duration / 60
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            return "${hours}h ${minutes}m"
        }
}

data class Section(
    val departure_date_time: String,
    val arrival_date_time: String,
    val from: StopPoint?, // Make StopPoint nullable
    val to: StopPoint?,   // Make StopPoint nullable
    val display_informations: DisplayInformation?
)

data class StopPoint(
    val id: String,
    val name: String?, // Make name nullable
    val coord: Coordinates,
    val stop_point: StopPointDetail? = null,
    val administrative_region: AdministrativeRegion? = null
)

data class StopPointDetail(
    val id: String,
    val name: String,
    val label: String,
    val coord: Coordinates,
    val links: List<Link>,
    val administrative_regions: List<AdministrativeRegion>,
    val stop_area: StopArea,
    val equipments: List<String>
)

data class AdministrativeRegion(
    val id: String,
    val name: String,
    val level: Int,
    val zip_code: String,
    val label: String,
    val insee: String,
    val coord: Coordinates
)

data class StopArea(
    val id: String,
    val name: String,
    val codes: List<Code>,
    val timezone: String,
    val label: String,
    val coord: Coordinates,
    val links: List<Link>
)

data class Code(
    val type: String,
    val value: String
)

data class Link(
    val href: String,
    val templated: Boolean,
    val rel: String,
    val type: String
)

data class Coordinates(
    val lon: String,
    val lat: String
)

data class DisplayInformation(
    val commercial_mode: String,
    val network: String,
    val direction: String,
    val label: String,
    val physical_mode: String
)

data class Co2Emission(
    val value: Double,
    val unit: String
)

data class Fare(
    val total: FareTotal?
)

data class FareTotal(
    val value: String
)