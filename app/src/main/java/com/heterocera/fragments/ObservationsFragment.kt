package com.heterocera.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.heterocera.R
import com.heterocera.adapters.ObservationsListAdapter
import com.heterocera.database.HeteroceraDatabase
import com.heterocera.database.Repository
import com.heterocera.viewmodel.ObservationViewModel
import com.heterocera.viewmodel.ObservationViewModelFactory

class ObservationsFragment : Fragment() {
    private lateinit var activityContext: Context
    private lateinit var obsListAdapterReference : ObservationsListAdapter
    val database by lazy { HeteroceraDatabase.getDatabase(activityContext) }
    private val repository by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    private val observationViewModel: ObservationViewModel by viewModels {
        ObservationViewModelFactory(repository)
    }
    private lateinit var recyclerView : RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
        activityContext.theme.applyStyle(R.style.Theme_Heterocera,true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView = view.findViewById(R.id.observations_recycler_view)
        val searchView =
            view.findViewById<androidx.appcompat.widget.SearchView>(R.id.observations_search_bar)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            observationViewModel.observationsArray.collect {
                val observationsListAdapter =
                    ObservationsListAdapter(it,this@ObservationsFragment)
                obsListAdapterReference = observationsListAdapter
                recyclerView.adapter = observationsListAdapter
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                obsListAdapterReference.filter.filter(newText)
                recyclerView.adapter=obsListAdapterReference
                return true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_observations, container, false)
    }

}