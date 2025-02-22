package com.project.triply.models.api
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class FlixbusResponse(
    val response_uuid: String,
    val trips: List<TripArray>
)

data class TripArray(
    val departure_city_id: String,
    val arrival_city_id: String,
    val date: String,
    // Vu que l'id est dynamique : on map
    val results: Map<String, TripResult>
)

data class TripResult(
    val uid: String,
    val status: String,
    val transfer_type: String,
    val transfer_type_key: String,
    val provider: String,
    val departure: StationInfo,
    val arrival: StationInfo,
    val duration: Duration,
    val price: Price,
    val remaining: Remaining?,
    val available: Available,
    val legs: List<Leg>,
    val restrictions: Restrictions,
    val intermediate_stations_count: Int
)

data class StationInfo(
    val date: String,
    val city_id: String,
    val station_id: String,
) {val time: String
    @RequiresApi(Build.VERSION_CODES.O)
    get() {
        val dateTime = ZonedDateTime.parse(date) // Parse la chaîne ISO 8601
        val formatter = DateTimeFormatter.ofPattern("HH:mm") // Format pour l'heure
        return dateTime.format(formatter) // Retourne l'heure au format HH:mm
    }

}

data class Duration(
    val hours: Int,
    val minutes: Int
)

data class Price(
    val total: Double,
    val original: Double,
    val average: Double,
    val total_with_platform_fee: Double,
    val average_with_platform_fee: Double
)

data class Remaining(
    val seats_left_at_price: Int?,
    val seats: Int?,
    val bike_slots: Int,
    val capacity: String
)

data class Available(
    val seats: Int,
    val bike_slots: Int
)

data class Leg(
    val departure: StationInfo,
    val arrival: StationInfo,
    val means_of_transport: String,
    val amenities: List<String>,
    val is_marketplace: Boolean,
    val operator_id: String,
    val brand_id: String,
    val vehicle_details: List<String>
)

data class Restrictions(
    val sale_restriction: Boolean,
    val info_title: String,
    val info_title_hint: String,
    val info_message: String,
    val bikes_allowed: Boolean
)

