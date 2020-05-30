package dev.example.ru.locationtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListOfLocationsAdapter: RecyclerView.Adapter<ListOfLocationsAdapter.ViewHolder>() {
    var locations: ArrayList<LocationModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_distance, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return locations.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.interval_number)
        val meters: TextView = view.findViewById(R.id.interval_length)
    }
}