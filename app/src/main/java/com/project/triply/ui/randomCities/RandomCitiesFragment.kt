package com.project.triply.ui.randomCities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
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

        // créer une liste qui va stocker les villes
        val citiesList = arrayListOf<RandomCitiesModel>()

        citiesList.add(RandomCitiesModel("Paris",
            "https://cdn.pixabay.com/photo/2015/10/06/18/26/eiffel-tower-975004_960_720.jpg"))

        citiesList.add(RandomCitiesModel("Calais",
            "https://cdn.pixabay.com/photo/2018/04/25/07/46/port-3348887_1280.jpg"))

        citiesList.add(RandomCitiesModel("Rome",
            "https://cdn.pixabay.com/photo/2020/03/31/23/27/rome-4989538_1280.jpg"))

        citiesList.add(RandomCitiesModel("Londres",
            "https://cdn.pixabay.com/photo/2022/02/15/13/00/building-7014904_960_720.jpg"))

        // recycler view
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerview)
        val spanCount = 2 // 2 columns
        val spacing = 20 // 20px spacing
        val includeEdge = false
        recyclerView?.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        recyclerView?.adapter = RandomCitiesAdapter(this, citiesList)


        return view
    }
}