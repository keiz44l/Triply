package com.project.triply.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.triply.R
import com.project.triply.models.api.Trip

class TripAdapter(private val trips: List<Trip>) :
    RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.bind(trip)
    }

    override fun getItemCount(): Int = trips.size

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDeparture: TextView = itemView.findViewById(R.id.textViewDeparture)
        private val textViewArrival: TextView = itemView.findViewById(R.id.textViewArrival)
        private val textViewDuration: TextView = itemView.findViewById(R.id.textViewDuration)
        private val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)

        fun bind(trip: Trip) {
            textViewDeparture.text = "Departure: ${trip.departureTime}"
            textViewArrival.text = "Arrival: ${trip.arrivalTime}"
            textViewDuration.text = "Duration: ${trip.duration}"
            textViewPrice.text = "Price: €${trip.price}"
        }
    }
}
