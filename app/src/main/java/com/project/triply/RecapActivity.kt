package com.project.triply

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.triply.models.api.Trip
import com.project.triply.utils.adapter.TripAdapter

class RecapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.travel_recap)

        val trips = intent.getParcelableArrayListExtra<Trip>("trips") ?: emptyList()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRecap)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val onTripSelected: (Trip) -> Unit = { selectedTrip ->
            // Handle the trip selection here
            Log.d("TripSelection", "Selected trip: $selectedTrip")
        }
        recyclerView.adapter = TripAdapter(
            trips,
            onTripSelected = onTripSelected
        )

        findViewById<TextView>(R.id.textViewRecapTitle).text = "Trip Summary"

        // Retrieve origin and destination from the intent
        val origin = intent.getStringExtra("origin") ?: "Unknown City"
        val destination = intent.getStringExtra("destination") ?: "Unknown City"

        // Set the departure information
        val textViewDepartureInfo = findViewById<TextView>(R.id.textViewDepartureInfo)
        textViewDepartureInfo.text = "From: $origin to $destination"

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
