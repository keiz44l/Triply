package com.project.triply

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.project.triply.data.FlixbusAPI
import com.project.triply.models.api.FlixbusResponse
import com.project.triply.models.api.Trip
import com.project.triply.utils.adapter.TripAdapter
import org.json.JSONObject

class ResultsActivity : AppCompatActivity() {

    private val flixbusAPI = FlixbusAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Initialize the API
        flixbusAPI.initialize(this)

        // Set up RecyclerView
        val recyclerViewTrips = findViewById<RecyclerView>(R.id.recyclerViewTrips)
        recyclerViewTrips.layoutManager = LinearLayoutManager(this)

        fetchFlixbusSchedule(recyclerViewTrips)
    }

    private fun fetchFlixbusSchedule(recyclerView: RecyclerView) {
        flixbusAPI.fetchSchedule(
            fromCityId = "40de8044-8646-11e6-9066-549f350fcb0c",
            toCityId = "40dea87d-8646-11e6-9066-549f350fcb0c",
            departureDate = "16.02.2025",
            onSuccess = { response ->
                handleSuccessResponse(response, recyclerView)
            },
            onError = { error ->
                handleError(error)
            }
        )
    }

    private fun handleSuccessResponse(response: JSONObject, recyclerView: RecyclerView) {
        try {
            // Parse JSON response to FlixbusResponse object
            val gson = Gson()
            val flixbusResponse: FlixbusResponse = gson.fromJson(response.toString(), FlixbusResponse::class.java)

            // debugging
            Log.d("API", "Response: ${response.toString()}")
            Log.d("API", "Parsed Response: ${flixbusResponse}")

            // Flatten trips and map the results
            val trips = flixbusResponse.trips.flatMap { trip ->
                trip.results.values.map { tripResult ->
                    Trip(
                        departureTime = tripResult.departure.date,
                        arrivalTime = tripResult.arrival.date,
                        duration = "${tripResult.duration.hours}h ${tripResult.duration.minutes}m",
                        price = tripResult.price.total
                    )
                }
            }

            // Update RecyclerView with the data
            recyclerView.adapter = TripAdapter(trips)

        } catch (e: Exception) {
            Log.e("API", "Error parsing response: ${e.message}")
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show()
        }
    }






    private fun handleError(error: String) {
        Log.e("API", "Error fetching schedule: $error")
        Toast.makeText(this, "Failed to fetch schedule: $error", Toast.LENGTH_SHORT).show()
    }
}
