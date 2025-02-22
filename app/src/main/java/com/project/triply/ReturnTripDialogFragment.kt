package com.project.triply

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.triply.databinding.DialogReturnTripBinding
import com.project.triply.models.api.Trip
import com.project.triply.utils.adapter.TripAdapter

class ReturnTripDialogFragment : DialogFragment() {

    private lateinit var tripAdapter: TripAdapter
    private var tripList: List<Trip> = emptyList()
    private var onTripSelected: ((Trip) -> Unit)? = null

    // Interface to pass the selected trip back to the parent activity
    fun setTripList(trips: List<Trip>, onTripSelected: (Trip) -> Unit) {
        this.tripList = trips
        this.onTripSelected = onTripSelected
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogReturnTripBinding.inflate(inflater, container, false)

        // Set up RecyclerView to show trips
        binding.recyclerViewTrips.layoutManager = LinearLayoutManager(requireContext())
        tripAdapter = TripAdapter(tripList) { selectedTrip ->
            onTripSelected?.invoke(selectedTrip)
            dismiss() // Close the dialog once a trip is selected
        }
        binding.recyclerViewTrips.adapter = tripAdapter

        return binding.root
    }
}
