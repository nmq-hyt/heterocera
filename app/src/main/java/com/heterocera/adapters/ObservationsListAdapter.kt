package com.heterocera.adapters

import android.content.Context
import android.graphics.Bitmap
//import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.heterocera.database.Observations
import com.heterocera.R
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ObservationsListAdapter(private val dataSet: List<Observations>,
                              private val fragment: Fragment
): ListAdapter<Observations, ObservationsListAdapter.ViewHolder>(
    ObservationsDiffCallback
), Filterable{

    var observationsFilterList = ArrayList<Observations>(dataSet)
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timestampTextView : TextView
        val commonNameTextView : TextView
        val latlngTextView : TextView
        val imageView : ImageView
        init{
            timestampTextView = view.findViewById(R.id.observation_item_timestamp)
            commonNameTextView = view.findViewById(R.id.observation_item_common_name)
            latlngTextView = view.findViewById(R.id.observation_item_lat_lng)
            imageView = view.findViewById(R.id.observation_image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.observation_item,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.commonNameTextView.text = observationsFilterList[position].ObservationSpeciesCommonName
        val instant = Instant.ofEpochMilli(observationsFilterList[position].DateTimestamp!!.toLong())
        val dateTimeStamp = OffsetDateTime.ofInstant(instant, ZoneId.of("GMT+1"))
        holder.timestampTextView.text = dateTimeStamp.format(DateTimeFormatter.ISO_DATE_TIME).toString()
        // much more efficient to use glide
        // forcible clear the imageView first
        // to guarantee any pre-used resources are clear
        Glide.with(fragment).clear(holder.imageView)
        Glide.with(fragment).load(Uri.parse(observationsFilterList[position].ObservationImageLink))
            .into(holder.imageView)
        holder.latlngTextView.text = StringBuilder("Lat: ")
            .append(observationsFilterList[position].ObservationLat).append(" Lng: ")
            .append(observationsFilterList[position].ObservationLng)
        holder.itemView.setOnClickListener{
            val argsBundle = Bundle(1)
            argsBundle.putString("uuid",observationsFilterList[position].UniqueId)
            holder.itemView.findNavController()
                .navigate(R.id.action_observationsFragment_to_observationRecordFragment2,argsBundle)
        }
    }
    override fun getItemCount(): Int {
        // you have to implement this or it won't load a list in the recyclerview
        // yet if you don't implement it the IDE doesn't give you a warning
        // lead to a completely nonsensical bug
        return observationsFilterList.size
    }

    override fun getFilter(): Filter {
        // in order to filter a list of something,
        // you must make a filter
        // which in basic terms i mean to say
        // "create a boolean statement that matches what you want"
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val resultList = ArrayList<Observations>(OBSERVATIONS_LIST_SIZE)
                if (constraint.isNullOrEmpty()) {
                    observationsFilterList = ArrayList(dataSet)
                } else {
                    for (observation in observationsFilterList) {
                        val dateTimeTruth:Boolean =
                            observation.DateTimestamp?.contains(constraint,true) == true
                        val uuidTruth: Boolean =
                            observation.UniqueId.contains(constraint,false)
                        val speciesTruth: Boolean =
                            observation.ObservationSpeciesCommonName!!
                                .contains(constraint,true)

                        Log.d("ObservationsLogging",observation.ObservationSpeciesCommonName)


                        if (speciesTruth) {
                            resultList.add(observation)
                        }
                    }
                }
                val results = FilterResults()
                results.values = resultList
                return results
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                observationsFilterList = results?.values as ArrayList<Observations>
                notifyDataSetChanged()
            }

}    }
    companion object{
        const val OBSERVATIONS_LIST_SIZE=1000
    }
}

object ObservationsDiffCallback : DiffUtil.ItemCallback<Observations>() {
    override fun areItemsTheSame(oldItem: Observations, newItem: Observations): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Observations, newItem: Observations): Boolean {
        return oldItem.UniqueId == newItem.UniqueId
    }
}