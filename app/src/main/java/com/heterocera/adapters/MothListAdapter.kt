package com.heterocera.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.heterocera.R
import com.heterocera.database.MothSpecies

class MothListAdapter(private val dataSet: List<MothSpecies>, val imgMap: HashMap<String, Int>) : ListAdapter<MothSpecies, MothListAdapter.ViewHolder>(
    MothDiffCallback
), Filterable{
// adapters are class of sub-components which usally implement
    var mothFilterList = ArrayList<MothSpecies>(dataSet)

   inner class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
         val textView : TextView
         val textView2 : TextView
         val imageView : ImageView
        init{
            textView = view.findViewById(R.id.moth_common_text)
            textView2 = view.findViewById(R.id.moth_formal_text)
            imageView = view.findViewById(R.id.moth_image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.moth_item,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = mothFilterList[position].SpeciesVulgarName
        holder.textView2.text = mothFilterList[position].SpeciesFormalName
        var testString = mothFilterList[position].ReferencePhotographFileString

        // awful string management code
        // removing this preprocessing was attempted
        // in database but it never stuck
        testString = testString?.
        removePrefix("/home/nmq-hyt/AndroidStudioProjects/Heterocera/app/src/main/assets/")
            ?.removeSuffix(".jpeg")
        val rInt : Int? = imgMap[testString]
        holder.imageView.setImageResource(rInt!!)
        holder.itemView.setOnClickListener {
            val argsBundle:Bundle = Bundle(1)
            argsBundle.putString("formal_name",mothFilterList[position].SpeciesFormalName)
            Log.d("MothListAdapter",holder.textView2.toString())
            //navigate to the entry specified
            holder.itemView.findNavController()
                .navigate(R.id.action_encyclopediaFragment_to_encyclopediaEntryFragment,argsBundle)
        }
    }

    override fun getItemCount(): Int {
        return mothFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val resultList = ArrayList<MothSpecies>(ENCYCLOPEDIA_SIZE)
                if (constraint.isNullOrEmpty()) {
                    mothFilterList = ArrayList(dataSet)
                } else {
                    for (moth in mothFilterList) {
                        if (moth.SpeciesVulgarName.contains(constraint,true)) {
                            resultList.add(moth)
                        }
                    }
                }
                val results = FilterResults()
                results.values=resultList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mothFilterList = results?.values as ArrayList<MothSpecies>
                notifyDataSetChanged()
            }

        }
    }

    companion object{
        const val ENCYCLOPEDIA_SIZE=100
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


