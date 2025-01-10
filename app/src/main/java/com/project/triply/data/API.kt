package com.project.triply.data

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
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
                "&include_after_midnight_rides=1" +
                "&disable_distribusion_trips=0" +
                "&disable_global_trips=0"

        // Create the request
        val request = JsonObjectRequest(
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
                Log.e("API", "Error fetching schedule: $errorMsg")
            }
        )

        // Add the request to the queue
        requestQueue?.add(request) ?: onError("Request queue is not initialized")
    }
}
