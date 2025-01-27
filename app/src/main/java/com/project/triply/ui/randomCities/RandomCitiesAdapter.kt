package com.project.triply.ui.randomCities

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.triply.R
import com.project.triply.ui.randomCities.RandomCitiesModel
import kotlin.math.min

class RandomCitiesAdapter(
    private val fragment: androidx.fragment.app.Fragment,
    private val citiesList: List<RandomCitiesModel>,
) : RecyclerView.Adapter<RandomCitiesAdapter.ViewHolder> (){



    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val citiesImage: ImageView = view.findViewById<ImageView>(R.id.cities_image)
        val citiesText: TextView = view.findViewById<TextView>(R.id.cities_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.random_cities_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int{
        return min(citiesList.size, 4) // afficher 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCity = citiesList[position]

        holder.citiesText.text = currentCity.name
        Glide.with(fragment).load(Uri.parse(currentCity.imageURL)).into(holder.citiesImage)
    }
}