package com.project.triply.ui.randomCities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.project.triply.R

class RandomCitiesFragment (
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.random_cities_fragment, container, false)

        //créer une liste qui va stocker les villes
        val citiesList = arrayListOf<RandomCitiesModel>()

        // recycler view
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerview)
        val spanCount = 2 // 2 columns
        val spacing = 20 // 20px spacing
        val includeEdge = false
        recyclerView?.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        recyclerView?.adapter = RandomCitiesAdapter(this, citiesList)

        // se connecter à Firestore
        val db = Firebase.firestore

        db.collection("randomCities")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val city = document.toObject(RandomCitiesModel::class.java)

                    if (city != null) {
                        citiesList.add(city)
                    }
                }
                citiesList.shuffle()
                recyclerView?.adapter = RandomCitiesAdapter(this, citiesList)
            }
            .addOnFailureListener{ exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        return view
    }
}