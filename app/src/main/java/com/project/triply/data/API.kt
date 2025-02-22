package com.project.triply.data

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class FlixbusAPI {

    private var requestQueue: RequestQueue? = null

    fun initialize(context: Context) {
        this.requestQueue = Volley.newRequestQueue(context)
    }

    fun fetchSchedule(
        fromCityId: String,
        toCityId: String,
        departureDate: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        // Update the URL to the official FlixBus API
        val url = "https://global.api.flixbus.com/search/service/v4/search" +
                "?from_city_id=$fromCityId" +
                "&to_city_id=$toCityId" +
                "&departure_date=$departureDate" +
                "&products=%7B%22adult%22%3A1%2C%22children%22%3A0%2C%22bike_slot%22%3A0%7D" +
                "&currency=USD" +
                "&locale=en_US" +
                "&search_by=cities" +
                "&include_after_midnight_rides=0" +
                "&disable_distribusion_trips=0" +
                "&disable_global_trips=0"
        Log.d("API", "URL: $url")
        // Create the request
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Handle success
                onSuccess(response)
                Log.d("FlixbusAPI", "Response: $response")

            },
            { error ->
                // Handle error
                val errorMsg = error.message ?: "Unknown error"
                onError(errorMsg)
                Log.e("API", "Error fetching schedule: $errorMsg")
                Log.d("FlixbusAPI", "Response: $error")

            }
        )

        // Add the request to the queue
        requestQueue?.add(request) ?: onError("Request queue is not initialized")
    }

    fun fetchCityId(
        query: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "https://global.api.flixbus.com/search/autocomplete/cities" +
                "?q=$query" +
                "&flixbus_cities_only=false" +
                "&stations=true" +
                "&popular_stations=true"

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    if (response.length() > 0) {
                        val firstCity = response.getJSONObject(0)
                        val id = firstCity.getString("id")
                        onSuccess(id)
                    } else {
                        onError("No results found")
                    }
                } catch (e: Exception) {
                    onError("Error parsing response: ${e.message}")
                    Log.e("API", "Error parsing response", e)
                }
            },
            { error ->
                val errorMsg = error.message ?: "Unknown error"
                onError(errorMsg)
                Log.e("API", "Error fetching city ID: $errorMsg")
            }
        )

        requestQueue?.add(request) ?: onError("Request queue is not initialized")
    }

}

class NavitiaAPI {
    private var requestQueue: RequestQueue? = null

    fun initialize(context: Context) {
        this.requestQueue = Volley.newRequestQueue(context)
    }

    fun fetchJourney(
        from: String,
        to: String,
        datetime: String,
        onSuccess: (JSONObject) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "https://api.navitia.io/v1/coverage/sncf/journeys" +
                "?datetime_represents=departure" +
                "&datetime=$datetime" +
                "&from=$from" +
                "&to=$to"

        val request = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Handle success
                onSuccess(response)
            },
            { error ->
                // Handle error
                val errorMsg = error.message ?: "Unknown error"
                onError(errorMsg)
                Log.e("API", "Error fetching journey: $errorMsg")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Basic YTY2NDM1MTQtNGIwNi00ZGM5LWFmMjYtNWRmZDVlMzEyMTBlOg=="
                return headers
            }
        }

        // Add the request to the queue
        requestQueue?.add(request) ?: onError("Request queue is not initialized")
    }

    fun getIdFromCity(cityName: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val url = "https://api.navitia.io/v1/coverage/sncf/places?q=$cityName"

        val request = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    val placesArray = response.getJSONArray("places")
                    if (placesArray.length() > 0) {
                        val firstPlace = placesArray.getJSONObject(0)
                        val id = firstPlace.getString("id")
                        onSuccess(id)
                    } else {
                        onError("No places found for the given city name.")
                    }
                } catch (e: Exception) {
                    onError("Error parsing response: ${e.message}")
                }
            },
            { error ->
                val errorMsg = error.message ?: "Unknown error"
                onError(errorMsg)
                Log.e("API", "Error fetching city ID: $errorMsg")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Basic YTY2NDM1MTQtNGIwNi00ZGM5LWFmMjYtNWRmZDVlMzEyMTBlOg=="
                return headers
            }
        }

        // Add the request to the queue
        requestQueue?.add(request) ?: onError("Request queue is not initialized")
    }
}


