package com.project.triply

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar

class SearchActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        // Eléments de base générés automatiquement
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/



        // Partie ajoutée manuellement


        /*  Création d'un bouton pour gérer les préférences de transport  */
        var checkedTrain = false
        var checkedBus = false
        var checkedPlane = false
        var checkedCar = false

        var options = arrayOf("Train","Bus","Plane","Car")
        var isOptionChecked = arrayOf(checkedTrain,checkedBus,checkedPlane,checkedCar)

        val boutonPreferences: Button = findViewById(R.id.boutonPreferences)

        boutonPreferences.setOnClickListener {
            preferencesWindow(options,isOptionChecked)
            Log.v("test","Bouton cliqué")
        }


        /*  Faire apparaître ou disparaître l'option d'ajouter une date de retour  */
        /*  selon si l'option est cochée ou non                                    */
        val returnDateBox: CardView = findViewById(R.id.returnDateBox)

        findViewById<CheckBox>(R.id.checkbox)
            .setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    returnDateBox.visibility = View.GONE
                } else {
                    returnDateBox.visibility = View.VISIBLE
                }
            }


        /*  Récupération des dates d'aller et de retour  */
        val departureDate: EditText = findViewById(R.id.departureDate)
        val returnDate: EditText = findViewById(R.id.returnDate)


        /*  Récupération des date du jour et du lendemain  */
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayPlusOne = day + 1

        val today = "$day/$month/$year"
        val tomorrow = "$dayPlusOne/$month/$year"


        /*  Initialisation des dates d'aller et de retour avec les date du jour et du lendemain  */
        departureDate.setText(today)
        returnDate.setText(tomorrow)


        /*  Définition des actions de sélection des dates du voyage  */
        departureDate.setOnClickListener {showDatePickerDialog(departureDate)}
        returnDate.setOnClickListener {showDatePickerDialog(returnDate)}


        /*  Récupération d'informations d'éléments de la page  */
        val texteSaisieFrom: EditText = findViewById(R.id.texteSaisieFrom)
        val texteSaisieTo: EditText = findViewById(R.id.texteSaisieTo)
        val boutonValider: Button = findViewById(R.id.boutonValider)

        /*  #######################################################  */
        /*  ############# Elément de test (à retirer) #############  */
        /*  #######################################################  */
        // val texteAffiche: TextView = findViewById(R.id.texteAffiche)
        /*  #######################################################  */
        /*  #######################################################  */


        /*  Définition des actions à l'appui sur le bouton de validation  */
        boutonValider.setOnClickListener {
            /*  Récupération d'informations d'éléments de la page  */
            val origin = texteSaisieFrom.text.toString()
            val destination = texteSaisieTo.text.toString()
            val departureDate2 = departureDate.text.toString()
            val returnDate2 = returnDate.text.toString()
            val isChecked = findViewById<CheckBox>(R.id.checkbox).isChecked


            /*  #######################################################  */
            /*  ############# Elément de test (à retirer) #############  */
            /*  #######################################################  */
            var texte = ""
            /*  #######################################################  */
            /*  #######################################################  */


            /*  Vérification que la date de départ est après celle du jour courant  */
            if (isBBeforeA(today,departureDate2)) {
                Snackbar.make(
                    findViewById(android.R.id.content),
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
                    texte = "De $origin à $destination\nle $departureDate2 au $returnDate2 $preferences"
                }

            }
            /*  Vérification que la date de retour est après celle du départ  */
            else if (isBBeforeA(departureDate2, returnDate2)) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Choose a return date after the departure date.",
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setBackgroundTint(Color.RED)
                    setTextColor(Color.WHITE)
                    show()
                }
            }
            else {
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
                    texte = "De $origin à $destination\ndu $departureDate2 au $returnDate2 $preferences"
                }
            }

            /*  #######################################################  */
            /*  ############# Elément de test (à retirer) #############  */
            /*  #######################################################  */
            // texteAffiche.text = texte
            /*  #######################################################  */
            /*  #######################################################  */
        }
    }



    private fun showDatePickerDialog(dateEditText: EditText) {
        /** Fonction permettant d'afficher l'outil de sélection de la date **/

        /*  Récupération de la date du jour  */
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)


        /*  Initialisation de l'affichage du calendrier de saisie de la date  */
        val datePickerDialog = DatePickerDialog(
            this,
            { view, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                dateEditText.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )


        /*  Affichage du calendrier de saisie de la date  */
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
        /** Fonction permettant d'afficher la fenêtre des préférences **/

        val builder = AlertDialog.Builder(this)
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