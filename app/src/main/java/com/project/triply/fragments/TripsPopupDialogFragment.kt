package com.project.triply

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.triply.models.api.Trip
import com.project.triply.utils.adapter.TripAdapter

class TripsPopupDialogFragment : DialogFragment() {

    private var tripList: List<Trip> = emptyList()
    private var onTripSelected: ((Trip) -> Unit)? = null
    private var title: String = "Available Trips"

    /**
     * Set the data for the dialog.
     *
     * @param trips The list of trips to display.
     * @param title The title of the popup.
     * @param onTripSelected Callback when a trip is selected.
     */
    fun setData(trips: List<Trip>, title: String, onTripSelected: (Trip) -> Unit) {
        this.tripList = trips
        this.onTripSelected = onTripSelected
        this.title = title
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_trips, null)
        val recyclerViewTrips = view.findViewById<RecyclerView>(R.id.recyclerViewDialogTrips)
        recyclerViewTrips.layoutManager = LinearLayoutManager(context)
        recyclerViewTrips.adapter = TripAdapter(tripList) { selectedTrip ->
            onTripSelected?.invoke(selectedTrip)
            dismiss() // Close the dialog after selection
        }
        builder.setView(view)
            .setTitle(title)
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
        return builder.create()
    }
}
