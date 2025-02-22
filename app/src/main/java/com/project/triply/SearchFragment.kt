package com.project.triply

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment(R.layout.fragment_search) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var options = arrayOf("Train", "Bus", "Plane", "Car")
        var isOptionChecked = BooleanArray(options.size)

        val boutonPreferences: Button = view.findViewById(R.id.boutonPreferences)

        boutonPreferences.setOnClickListener {
            preferencesWindow(options, isOptionChecked)
            Log.v("test", "Bouton cliqué")
        }

        val returnDateBox: CardView = view.findViewById(R.id.returnDateBox)
        view.findViewById<CheckBox>(R.id.checkbox)
            .setOnCheckedChangeListener { _, isChecked ->
                returnDateBox.visibility = if (isChecked) View.GONE else View.VISIBLE
            }

        val departureDate: EditText = view.findViewById(R.id.departureDate)
        val returnDate: EditText = view.findViewById(R.id.returnDate)

        val calendar = Calendar.getInstance()
        val today = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"

        departureDate.setText(today)
        returnDate.setText(tomorrow)

        departureDate.setOnClickListener { showDatePickerDialog(departureDate) }
        returnDate.setOnClickListener { showDatePickerDialog(returnDate) }

        val texteSaisieFrom: EditText = view.findViewById(R.id.texteSaisieFrom)
        val texteSaisieTo: EditText = view.findViewById(R.id.texteSaisieTo)
        val boutonValider: Button = view.findViewById(R.id.boutonValider)

        boutonValider.setOnClickListener {
            val origin = texteSaisieFrom.text.toString()
            val destination = texteSaisieTo.text.toString()
            val departureDate2 = departureDate.text.toString()
            val returnDate2 = returnDate.text.toString()
            val isChecked = view.findViewById<CheckBox>(R.id.checkbox).isChecked

            if (isBBeforeA(today, departureDate2)) {
                showSnackbar(view, "Choose a departure date after today's date.")
            } else if (isChecked) {
                val preferences = getSelectedPreferences(options, isOptionChecked)
                startResultsActivity(origin, destination, departureDate2, returnDate2, preferences, !isChecked)
            } else if (isBBeforeA(departureDate2, returnDate2)) {
                showSnackbar(view, "Choose a return date after the departure date.")
            } else {
                val preferences = getSelectedPreferences(options, isOptionChecked)
                startResultsActivity(origin, destination, departureDate2, returnDate2, preferences, !isChecked)
            }
        }
    }

    private fun showDatePickerDialog(dateEditText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                dateEditText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun isBBeforeA(dateA: String, dateB: String): Boolean {
        val (dayA, monthA, yearA) = dateA.split("/").map { it.toInt() }
        val (dayB, monthB, yearB) = dateB.split("/").map { it.toInt() }
        return yearB < yearA || (yearB == yearA && (monthB < monthA || (monthB == monthA && dayB < dayA)))
    }

    private fun preferencesWindow(options: Array<String>, checkedItems: BooleanArray) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Preferences")

        builder.setMultiChoiceItems(options, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }

        builder.setNegativeButton("Exit") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNeutralButton("Reset") { dialog, _ ->
            checkedItems.fill(false)
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun getSelectedPreferences(options: Array<String>, checkedItems: BooleanArray): List<String> {
        return options.filterIndexed { index, _ -> checkedItems[index] }
    }

    private fun startResultsActivity(
        origin: String, destination: String, departureDate: String, returnDate: String,
        preferences: List<String>, returnMode: Boolean
    ) {
        val intent = Intent(requireContext(), ResultsActivity::class.java).apply {
            putExtra("origin", origin)
            putExtra("destination", destination)
            putExtra("departureDate", departureDate)
            putExtra("returnDate", returnDate)
            putStringArrayListExtra("preferences", ArrayList(preferences))
            putExtra("returnMode", returnMode)
        }
        startActivity(intent)
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).apply {
            setBackgroundTint(Color.RED)
            setTextColor(Color.WHITE)
            show()
        }
    }
}
