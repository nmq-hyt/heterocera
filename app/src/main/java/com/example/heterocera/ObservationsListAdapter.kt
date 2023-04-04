package com.example.heterocera

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.heterocera.database.Observations

class ObservationsListAdapter(private val dataSet: List<Observations>): ListAdapter<Observations,ObservationsListAdapter.ViewHolder>(ObservationsDiffCallback) {


    var onItemClick : ((Observations) -> Unit)? = null
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val formalNameTextView : TextView
        val commonNameTextView : TextView
        lateinit var descriptionTextView : TextView
        val imageView : ImageView
        init{
            formalNameTextView = view.findViewById(R.id.moth_formal_text)
            commonNameTextView = view.findViewById(R.id.moth_common_text)
            imageView = view.findViewById(R.id.moth_image_view)
            view.setOnClickListener {
                onItemClick?.invoke(dataSet[absoluteAdapterPosition])
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.observation_item,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.formalNameTextView.text = dataSet[position].ObservationSpeciesFormalName
        holder.commonNameTextView.text = dataSet[position].ObservationSpeciesCommonName
        holder.descriptionTextView.text = dataSet[position].ObservationDescription
        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(dataSet[position].ObservationImageLink))
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