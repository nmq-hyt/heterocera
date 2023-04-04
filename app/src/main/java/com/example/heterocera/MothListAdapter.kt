package com.example.heterocera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.heterocera.database.MothSpecies

class MothListAdapter(private val dataSet: List<MothSpecies>, val imgMap: HashMap<String, Int>) : ListAdapter<MothSpecies,MothListAdapter.ViewHolder>(MothDiffCallback){

    var onItemClick : ((MothSpecies) -> Unit)? = null

   inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
         val textView : TextView
         val textView2 : TextView
         val imageView : ImageView
        init{
            textView = view.findViewById(R.id.moth_common_text)
            textView2 = view.findViewById(R.id.moth_formal_text)
            imageView = view.findViewById(R.id.moth_image_view)
            view.setOnClickListener {
                onItemClick?.invoke(dataSet[absoluteAdapterPosition])
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.moth_item,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = dataSet[position].SpeciesVulgarName
        holder.textView2.text = dataSet[position].SpeciesFormalName
        var testString = dataSet[position].ReferencePhotographFileString
        testString = testString?.
        removePrefix("/home/nmq-hyt/AndroidStudioProjects/Heterocera/app/src/main/assets/")
            ?.removeSuffix(".jpeg")
        val rInt : Int? = imgMap[testString]
        holder.imageView.setImageResource(rInt!!)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}
object MothDiffCallback : DiffUtil.ItemCallback<MothSpecies>() {
    override fun areItemsTheSame(oldItem: MothSpecies, newItem: MothSpecies): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MothSpecies, newItem: MothSpecies): Boolean {
        return oldItem.SpeciesFormalName == newItem.SpeciesFormalName
    }
}