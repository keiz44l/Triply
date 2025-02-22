package com.project.triply.utils.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.project.triply.R
import com.project.triply.models.api.Trip

class TripAdapter(
    private var trips: List<Trip>,
    private val onTripSelected: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    fun updateTrips(newTrips: List<Trip>) {
        trips = newTrips
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDeparture: TextView = itemView.findViewById(R.id.textViewDeparture)
        private val textViewArrival: TextView = itemView.findViewById(R.id.textViewArrival)
        private val textViewDuration: TextView = itemView.findViewById(R.id.textViewDuration)
        private val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        private val textViewDepartureStation: TextView = itemView.findViewById(R.id.textViewDepartureStation)
        private val textViewArrivalStation: TextView = itemView.findViewById(R.id.textViewArrivalStation)
        private val textViewStops: TextView = itemView.findViewById(R.id.textViewStops)
        private val buttonToggleDetails: Button = itemView.findViewById(R.id.buttonToggleDetails)
        private val scrollViewDetails: ScrollView = itemView.findViewById(R.id.scrollViewDetails)

        @OptIn(UnstableApi::class)
        fun bind(trip: Trip) {
            // Check if departure time is unknown and display the city instead
            if (trip.departureTime == "Unknown") {
                textViewDeparture.text = "Departure: ${trip.departureStation ?: "Unknown"}"
            } else {
                textViewDeparture.text = "Departure: ${trip.departureTime}"
            }

            // Check if arrival time is unknown and display the city instead
            if (trip.arrivalTime == "Unknown") {
                textViewArrival.text = "Arrival: ${trip.arrivalStation ?: "Unknown"}"
            } else {
                textViewArrival.text = "Arrival: ${trip.arrivalTime}"
            }

            textViewDuration.text = "Duration: ${trip.duration}"

            // Show price only for bus trips
            if (trip.type == "bus") {
                textViewPrice.visibility = View.VISIBLE
                textViewPrice.text = "Price: €${trip.price}"
            } else {
                textViewPrice.visibility = View.GONE
            }

            // Set departure and arrival stations
            textViewDepartureStation.text = "Departure Station: ${trip.departureStation ?: "Unknown"}"
            textViewArrivalStation.text = "Arrival Station: ${trip.arrivalStation ?: "Unknown"}"

            // Handle trip selection when the card is clicked
            itemView.setOnClickListener {
                onTripSelected(trip) // Call the onTripSelected lambda
            }

            // Handle details toggle
            buttonToggleDetails.setOnClickListener {
                Log.d("DetailsToggle", "Button clicked for trip: ${trip.departureTime}")
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

            // Update additional information for Navitia trips
            if (trip.type == "train") {
                textViewStops.visibility = View.VISIBLE
                textViewStops.text = "Stops: ${trip.stops ?: "Unknown"}"
            } else {
                textViewStops.visibility = View.GONE
            }
        }
    }


}
