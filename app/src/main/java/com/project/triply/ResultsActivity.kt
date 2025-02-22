package com.project.triply

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.gson.Gson
import com.project.triply.data.FlixbusAPI
import com.project.triply.data.NavitiaAPI
import com.project.triply.models.api.FlixbusResponse
import com.project.triply.models.api.Journey
import com.project.triply.models.api.Trip
import com.project.triply.models.api.NavitiaResponse
import com.project.triply.utils.toMinutes

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale
import java.util.TreeSet

class ResultsActivity : AppCompatActivity() {

    private val flixbusAPI = FlixbusAPI()
    private val navitiaAPI = NavitiaAPI()
    private var selectedTrip: Trip? = null
    private var selectedReturnTrip: Trip? = null
    private var returnDate: String? = null
    private var origin: String? = null
    private var destination: String? = null
    private var isReturnMode: Boolean = false
    private var preferences: List<String> = emptyList()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Retrieve search parameters from the intent.
        origin = intent.getStringExtra("origin") ?: "N/A"
        destination = intent.getStringExtra("destination") ?: "N/A"
        val departureDate = intent.getStringExtra("departureDate") ?: "N/A"
        returnDate = intent.getStringExtra("returnDate") ?: "N/A"
        isReturnMode = intent.getBooleanExtra("returnMode", false)
        preferences = intent.getStringArrayListExtra("preferences") ?: emptyList()




        if (isReturnMode) {
            selectedReturnTrip = intent.getParcelableExtra("departureTrip")
        } else {
            selectedTrip = intent.getParcelableExtra("departureTrip")
        }



        Log.d(
            "SearchData",
            "Origin: $origin, Destination: $destination, Date: $departureDate, ReturnDate: $returnDate, ReturnMode: $isReturnMode"
        )

        flixbusAPI.initialize(this)
        navitiaAPI.initialize(this)

        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)
        textViewTitle.text = if (isReturnMode) "Select Return Trip" else "Select Departure Trip"

        val cardViewBus = findViewById<CardView>(R.id.cardViewBus)
        val cardViewTrain = findViewById<CardView>(R.id.cardViewTrain)

        // Fetch bus trips if departure date is valid
        if (departureDate != "N/A" && (preferences.contains("Bus") || preferences!!.isEmpty())) {
            fetchTripsForDisplay(origin, destination, departureDate, cardViewBus, "bus")
        } else {
            cardViewBus.visibility = View.GONE
        }

        // Fetch train trips if departure date is valid
        if (departureDate != "N/A" && (preferences.contains("Train") || preferences!!.isEmpty())) {
            navitiaAPI.getIdFromCity(origin!!,
                onSuccess = { originId ->
                    navitiaAPI.getIdFromCity(destination!!,
                        onSuccess = { destinationId ->
                            Log.d("PostalCode", "Origin ID: $originId, Destination ID: $destinationId")
                            fetchTrainTripsForDisplay(originId, destinationId, departureDate, cardViewTrain)
                        },
                        onError = { errorMsg ->
                            handleError("Error getting ID for destination: $errorMsg")
                        }
                    )
                },
                onError = { errorMsg ->
                    handleError("Error getting ID for origin: $errorMsg")
                }
            )
        } else {
            cardViewTrain.visibility = View.GONE
        }

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun navigateToRecap() {
        val intent = Intent(this, RecapActivity::class.java)
        val trips = arrayListOf<Trip>()
        selectedTrip?.let { trips.add(it) }
        selectedReturnTrip?.let { trips.add(it) }
        intent.putParcelableArrayListExtra("trips", trips)

        // Pass the origin and destination to the RecapActivity
        if (trips.size == 2) {
            intent.putExtra("origin", destination)
            intent.putExtra("destination", origin)
        } else {
            intent.putExtra("origin", origin)
            intent.putExtra("destination", destination)
        }


        startActivity(intent)
        finish()
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTripsForDisplay(
        origin: String?,
        destination: String?,
        date: String?,
        cardView: CardView,
        type: String
    ) {
        if (origin == null || destination == null || date == null) {
            handleError("Missing required parameters")
            cardView.visibility = View.GONE
            return
        }

        val formattedDate = formatDateToFlixbus(date)
        if (formattedDate == null) {
            handleError("Invalid date format: $date")
            cardView.visibility = View.GONE
            return
        }

        flixbusAPI.fetchCityId(origin, { fromCityId ->
            flixbusAPI.fetchCityId(destination, { toCityId ->
                Log.d("API", "Fetched City IDs: From: $fromCityId, To: $toCityId")
                flixbusAPI.fetchSchedule(
                    fromCityId = fromCityId,
                    toCityId = toCityId,
                    departureDate = formattedDate,
                    onSuccess = { response ->
                        Log.d("API", "Raw response: $response") // Log the raw response
                        try {
                            val gson = Gson()
                            val flixbusResponse: FlixbusResponse =
                                gson.fromJson(response.toString(), FlixbusResponse::class.java)
                            val trips = flixbusResponse.trips.flatMap { trip ->
                                trip.results.values.mapNotNull { tripResult ->
                                    Trip(
                                        departureTime = tripResult.departure.time ?: "Unknown",
                                        arrivalTime = tripResult.arrival.time ?: "Unknown",
                                        duration = "${tripResult.duration.hours}h ${tripResult.duration.minutes}m",
                                        price = tripResult.price.total?.toString() ?: "Unknown",
                                        type = type,
                                        bookingLink = "none",
                                        departureStation = origin,
                                        arrivalStation = destination
                                    )
                                }
                            }
                            if (trips.isEmpty()) {
                                cardView.visibility = View.GONE
                            } else {
                                updateCardView(cardView, trips, type)
                            }
                        } catch (e: Exception) {
                            Log.e("API", "Error parsing response: ${e.message}")
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onError = { error -> handleError(error) }
                )
            }, { error -> handleError("Error fetching destination city ID: $error") })
        }, { error -> handleError("Error fetching origin city ID: $error") })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTrainTripsForDisplay(
        origin: String?,
        destination: String?,
        date: String?,
        cardView: CardView
    ) {
        if (origin == null || destination == null || date == null) {
            handleError("Missing required parameters")
            cardView.visibility = View.GONE
            return
        }

        val currentDateTime = LocalDateTime.now()
        val formattedDate = if (isSameDay(date, currentDateTime)) {
            formatDateToNavitia(date, currentDateTime.hour)
        } else {
            formatDateToNavitia(date, 0) // Start from midnight if not today
        }

        if (formattedDate == null) {
            handleError("Invalid date format: $date")
            cardView.visibility = View.GONE
            return
        }

        // Fetch trips for each hour of the day
        val allTrips = TreeSet<Trip>(compareBy { it.departureTime }) // Use a TreeSet to avoid duplicates and maintain order
        for (hour in 0 until 24) {
            val hourlyFormattedDate = formatDateToNavitiaWithHour(date, hour)
            if (hourlyFormattedDate != null) {
                navitiaAPI.fetchJourney(origin, destination, hourlyFormattedDate,
                    onSuccess = { response ->
                        try {
                            val gson = Gson()
                            val navitiaResponse: NavitiaResponse = gson.fromJson(response.toString(), NavitiaResponse::class.java)
                            val trips = convertJourneysToTrips(navitiaResponse.journeys)
                            allTrips.addAll(trips)

                            // Update UI after all trips are fetched
                            if (hour == 23) {
                                updateCardView(cardView, allTrips.toList(), "train")
                            }
                        } catch (e: Exception) {
                            Log.e("API", "Error parsing response: ${e.message}")
                            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onError = { error -> handleError(error) }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isSameDay(dateString: String, currentDateTime: LocalDateTime): Boolean {
        val inputFormat = SimpleDateFormat("dd/M/yyyy", Locale.US)
        val date = inputFormat.parse(dateString) ?: return false
        val calendar = java.util.Calendar.getInstance()
        calendar.time = date
        return calendar.get(java.util.Calendar.DAY_OF_YEAR) == currentDateTime.dayOfYear
    }

    private fun formatDateToNavitia(dateString: String, hour: Int): String? {
        Log.d("API", "Received date string: $dateString")
        return try {
            val inputFormat = SimpleDateFormat("dd/M/yyyy", Locale.US)
            val outputFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US)
            val date = inputFormat.parse(dateString) ?: return null

            val calendar = java.util.Calendar.getInstance()
            calendar.time = date
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)

            Log.d("API", "Formatted date: ${outputFormat.format(calendar.time)}")
            outputFormat.format(calendar.time)
        } catch (e: Exception) {
            Log.e("API", "Date parsing error: ${e.message}")
            null
        }
    }

    private fun formatDateToNavitiaWithHour(dateString: String, hour: Int): String? {
        return try {
            val inputFormat = SimpleDateFormat("dd/M/yyyy", Locale.US)
            val outputFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.US)
            val date = inputFormat.parse(dateString) ?: return null

            val calendar = java.util.Calendar.getInstance()
            calendar.time = date
            calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)

            outputFormat.format(calendar.time)
        } catch (e: Exception) {
            Log.e("API", "Date parsing error: ${e.message}")
            null
        }
    }

    private fun formatDateToFlixbus(dateString: String): String? {
        Log.d("API", "Received date string: $dateString")
        return try {
            val inputFormat = SimpleDateFormat("dd/M/yyyy", Locale.US)
            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            val date = inputFormat.parse(dateString) ?: return null
            Log.d("API", "Formatted date: ${outputFormat.format(date)}")
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("API", "Date parsing error: ${e.message}")
            null
        }
    }

    private fun showTripsPopup(trips: List<Trip>, title: String, onTripSelected: (Trip) -> Unit) {
        val dialog = TripsPopupDialogFragment()
        dialog.setData(trips, title, onTripSelected)
        dialog.show(supportFragmentManager, "TripsPopupDialog")
    }

    private fun handleError(error: String) {
        Log.e("API", "Error: $error")
        Toast.makeText(this, "Failed to fetch data: $error", Toast.LENGTH_SHORT).show()
    }

    private fun updateCardView(cardView: CardView, trips: List<Trip>, type: String) {
        val textViewPrice = cardView.findViewById<TextView>(if (type == "bus") R.id.textViewPriceBus else R.id.textViewPriceTrain)
        val textViewDuration = cardView.findViewById<TextView>(if (type == "bus") R.id.textViewDurationBus else R.id.textViewDurationTrain)
        val textViewDepartureStation = cardView.findViewById<TextView?>(R.id.textViewDepartureStation)
        val textViewArrivalStation = cardView.findViewById<TextView?>(R.id.textViewArrivalStation)
        val textViewStops = cardView.findViewById<TextView?>(R.id.textViewStops)
        val buttonToggleDetails = cardView.findViewById<Button?>(R.id.buttonToggleDetails)
        val scrollViewDetails = cardView.findViewById<ScrollView>(R.id.scrollViewDetails)

        // Filter out "Unknown" prices and calculate the average of the remaining prices
        val validPrices = trips.mapNotNull {
            if (it.price == "Unknown") null else it.price.toDoubleOrNull()
        }
        val averagePrice = if (validPrices.isEmpty()) 0.0 else validPrices.average()

        val averageDurationInMinutes = trips.map { it.duration.toMinutes() }.average()
        val averageDuration = averageDurationInMinutes.toInt().toHoursAndMinutes()

        if (type == "bus") {
            textViewPrice.visibility = View.VISIBLE
            textViewPrice.text = if (validPrices.isEmpty()) "Price: Unknown" else "Price: €${String.format("%.2f", averagePrice)}"
        } else {
            textViewPrice.visibility = View.GONE
        }

        textViewDuration.text = "Duration: $averageDuration"

        if (type == "train") {
            // Update additional information for Navitia trips
            val firstTrip = trips.firstOrNull()
            firstTrip?.let { trip ->
                textViewDepartureStation?.visibility = View.VISIBLE
                textViewArrivalStation?.visibility = View.VISIBLE
                textViewStops?.visibility = View.VISIBLE

                textViewDepartureStation?.text = "Departure Station: ${trip.departureStation ?: "Unknown"}"
                textViewArrivalStation?.text = "Arrival Station: ${trip.arrivalStation ?: "Unknown"}"
                textViewStops?.text = "Stops: ${trip.stops ?: "Unknown"}"
            }
        }

        buttonToggleDetails?.setOnClickListener {
            Log.d("DetailsToggle", "Button clicked!")
            if (scrollViewDetails.visibility == View.VISIBLE) {
                scrollViewDetails.visibility = View.GONE
                buttonToggleDetails.text = "Show Details"
                Log.d("DetailsToggle", "Details hidden.")
            } else {
                scrollViewDetails.visibility = View.VISIBLE
                buttonToggleDetails.text = "Hide Details"
                Log.d("DetailsToggle", "Details shown.")
            }
        }

        cardView.setOnClickListener {
            showTripsPopup(trips, "Available $type Trips") { trip ->
                if (selectedTrip != null) {
                    selectedReturnTrip = trip
                }else selectedTrip = trip
                Log.d("TripSelection", "Selected trip: $trip")

                Log.d("$type Selection", "Selected $type trip: $selectedTrip")
                Log.d("$type Selection", "Selected return trip: $selectedReturnTrip")

                if (returnDate != "N/A" && isReturnMode) {
                    val intent = Intent(this, ResultsActivity::class.java).apply {
                        putExtra("origin", destination)
                        putExtra("destination", origin)
                        putExtra("departureDate", returnDate)
                        putExtra("returnDate", returnDate)
                        putExtra("returnMode", false)
                        putExtra("departureTrip", selectedTrip)
                        putExtra("preferences", ArrayList(preferences))

                    }
                    startActivity(intent)
                    finish()
                } else {
                    navigateToRecap()
                }
            }
        }
    }




    fun Int.toHoursAndMinutes(): String {
        val hours = this / 60
        val minutes = this % 60
        return "${hours}h ${minutes}m"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertJourneysToTrips(journeys: List<Journey>): List<Trip> {
        return journeys.map { journey ->
            val totalSeconds = journey.duration
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60

            // Extract additional information safely
            val departureStation = journey.sections.firstOrNull()?.from?.name?.takeIf { it.isNotEmpty() } ?: "Unknown"
            val arrivalStation = journey.sections.lastOrNull()?.to?.name?.takeIf { it?.isNotEmpty() == true } ?: "Unknown"
            val stops = journey.sections.joinToString { section ->
                val from = section.from?.name?.takeIf { it.isNotEmpty() } ?: "Unknown"
                val to = section.to?.name?.takeIf { it.isNotEmpty() } ?: "Unknown"
                "$from to $to"
            }

            Trip(
                departureTime = journey.departureTime,
                arrivalTime = journey.arrivalTime,
                duration = "${hours}h ${minutes}m",
                price = "Unknown",
                type = "train",
                bookingLink = "none",
                departureStation = departureStation,
                arrivalStation = arrivalStation,
                stops = stops
            )
        }
    }
}
