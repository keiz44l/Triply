package com.project.triply

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.triply.models.api.Trip
import com.project.triply.utils.adapter.TripAdapter

class TripsDialogFragment(private val trips: List<Trip>) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        // Inflate the layout for the dialog
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_trips, null)

        // Set up RecyclerView inside the dialog
        val recyclerViewTrips = view.findViewById<RecyclerView>(R.id.recyclerViewDialogTrips)
        recyclerViewTrips.layoutManager = LinearLayoutManager(context)
        val onTripSelected: (Trip) -> Unit = { selectedTrip ->
            // Handle the trip selection here
            Log.d("TripSelection", "Selected trip: $selectedTrip")
        }
        recyclerViewTrips.adapter = TripAdapter(trips,onTripSelected)

        // Set up the dialog
        builder.setView(view)
            .setTitle("Available Trips")
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}
