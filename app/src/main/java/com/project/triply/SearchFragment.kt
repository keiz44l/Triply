package com.project.triply

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class SearchFragment : Fragment(R.layout.fragment_search) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Création d'un bouton pour gérer les préférences de transport */
        var checkedTrain = false
        var checkedBus = false
        var checkedPlane = false
        var checkedCar = false

        var options = arrayOf("Train", "Bus", "Plane", "Car")
        var isOptionChecked = arrayOf(checkedTrain, checkedBus, checkedPlane, checkedCar)

        val boutonPreferences: Button = view.findViewById(R.id.boutonPreferences)

        boutonPreferences.setOnClickListener {
            preferencesWindow(options, isOptionChecked)
            Log.v("test", "Bouton cliqué")
        }

        /* Faire apparaître ou disparaître l'option d'ajouter une date de retour */
        val returnDateBox: CardView = view.findViewById(R.id.returnDateBox)

        view.findViewById<CheckBox>(R.id.checkbox)
            .setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    returnDateBox.visibility = View.GONE
                } else {
                    returnDateBox.visibility = View.VISIBLE
                }
            }

        /* Récupération des dates d'aller et de retour */
        val departureDate: EditText = view.findViewById(R.id.departureDate)
        val returnDate: EditText = view.findViewById(R.id.returnDate)

        /* Récupération des dates du jour et du lendemain */
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayPlusOne = day + 1

        val today = "$day/$month/$year"
        val tomorrow = "$dayPlusOne/$month/$year"

        /* Initialisation des dates d'aller et de retour */
        departureDate.setText(today)
        returnDate.setText(tomorrow)

        /* Définir les actions pour sélectionner des dates */
        departureDate.setOnClickListener { showDatePickerDialog(departureDate) }
        returnDate.setOnClickListener { showDatePickerDialog(returnDate) }

        /* Actions sur le bouton de validation */
        val texteSaisieFrom: EditText = view.findViewById(R.id.texteSaisieFrom)
        val texteSaisieTo: EditText = view.findViewById(R.id.texteSaisieTo)
        val boutonValider: Button = view.findViewById(R.id.boutonValider)
        val texteAffiche: TextView = view.findViewById(R.id.texteAffiche)

        /*  Définition des actions à l'appui sur le bouton de validation  */
        boutonValider.setOnClickListener {
            /*  Récupération d'informations d'éléments de la page  */
            val origin = texteSaisieFrom.text.toString()
            val destination = texteSaisieTo.text.toString()
            val departureDate2 = departureDate.text.toString()
            val returnDate2 = returnDate.text.toString()
            val isChecked = view.findViewById<CheckBox>(R.id.checkbox).isChecked


            /*  #######################################################  */
            /*  ############# Elément de test (à retirer) #############  */
            /*  #######################################################  */
            var texte = ""
            /*  #######################################################  */
            /*  #######################################################  */


            /*  Vérification que la date de départ est après celle du jour courant  */
            if (isBBeforeA(today, departureDate2)) {
                Snackbar.make(
                    view.findViewById(android.R.id.content),
                    "Choose a departure date after today's date.",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setBackgroundTint(Color.RED)  // Couleur de fond du Snackbar
                    setTextColor(Color.WHITE)
                    show()
                }
            }
            /*  Vérification si un retour est prévu  */
            else if (isChecked) {
                var preferences = "\nPréférences : "
                for (i in isOptionChecked.indices) {
                    if (isOptionChecked[i]) {
                        preferences += if (preferences == "\nPréférences : ") {
                            options[i]
                        } else {
                            ", " + options[i]
                        }
                    }
                }
                if (preferences == "\nPréférences : ") {
                    texte = "De $origin à $destination\nle $departureDate2"
                } else {
                    texte =
                        "De $origin à $destination\nle $departureDate2 au $returnDate2 $preferences"
                }

            }
            /*  Vérification que la date de retour est après celle du départ  */
            else if (isBBeforeA(departureDate2, returnDate2)) {
                Snackbar.make(
                    view.findViewById(android.R.id.content),
                    "Choose a return date after the departure date.",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setBackgroundTint(Color.RED)
                    setTextColor(Color.WHITE)
                    show()
                }
            } else {
                /*  Vérification de la présence de préférences  */
                var preferences = "\nPréférences : "
                for (i in isOptionChecked.indices) {
                    if (isOptionChecked[i]) {
                        preferences += if (preferences == "\nPréférences : ") {
                            options[i]
                        } else {
                            ", " + options[i]
                        }
                    }
                }


                /*  Stockage des informations récupérées au sein d'une chaîne de caractères  */
                if (preferences == "\nPréférences : ") {
                    texte = "De $origin à $destination\ndu $departureDate2 au $returnDate2"
                } else {
                    texte =
                        "De $origin à $destination\ndu $departureDate2 au $returnDate2 $preferences"
                }
            }
            texteAffiche.text = texte
        }
    }



    private fun showDatePickerDialog(dateEditText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(), // Utilisez requireContext() pour obtenir un context valide
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                dateEditText.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }



    private fun isBBeforeA(dateA: String, dateB: String): Boolean {
        /** Fonction permettant de déterminer si une date est avant une autre **/

        /*  Séparation de la date en une liste sous la forme : [jour, mois, année]  */
        var splitedDateA = dateA.split("/")
        var splitedDateB = dateB.split("/")


        /*  Initialisation de la variable de vérification de la fonction  */
        var bBeforeA = false


        /*  Vérification que la date A est avant la date B  */
        if (splitedDateB[2].toInt() < splitedDateA[2].toInt()) {
            bBeforeA = true
        } else if (splitedDateB[1].toInt() < splitedDateA[1].toInt()) {
            bBeforeA = true
        } else if (splitedDateB[0].toInt() < splitedDateA[0].toInt()) {
            bBeforeA = true
        }

        return bBeforeA
    }



    private fun preferencesWindow(options: Array<String>, checkedItems: Array<Boolean>) {
        val builder = AlertDialog.Builder(requireContext())  // Utilisez requireContext() ici
        builder.setTitle("Preferences")

        val checkedItemsPrimitive = checkedItems.map { it }.toBooleanArray()

        builder.setMultiChoiceItems(options, checkedItemsPrimitive) { _, which, isChecked ->
            checkedItems[which] = isChecked
        }

        builder.setNegativeButton("Exit") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNeutralButton("Reset") { dialog, _ ->
            for (i in checkedItems.indices) {
                checkedItems[i] = false
            }
            dialog.dismiss()
        }

        builder.create().show()
    }

}