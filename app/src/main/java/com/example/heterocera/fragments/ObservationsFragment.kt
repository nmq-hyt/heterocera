package com.example.heterocera.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.heterocera.MothListAdapter
import com.example.heterocera.ObservationsListAdapter
import com.example.heterocera.R
import com.example.heterocera.database.HeteroceraDatabase
import com.example.heterocera.database.MothSpecies
import com.example.heterocera.database.Repository

import com.example.heterocera.viewmodel.ObservationViewModel
import com.example.heterocera.viewmodel.ObservationViewModelFactory

class ObservationsFragment : Fragment() {
    private lateinit var activityContext: Context
    val database by lazy { HeteroceraDatabase.getDatabase(activityContext) }
    private val repository by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    private val observationViewModel: ObservationViewModel by viewModels<ObservationViewModel> {
        ObservationViewModelFactory(repository)
    }

    private lateinit var recyclerView : RecyclerView


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView  = view.findViewById(R.id.observations_recycler_view)
        val observationList = observationViewModel.observationsArray

        val observationsListAdapter = ObservationsListAdapter(observationList)
        recyclerView.adapter = observationsListAdapter
        observationsListAdapter.onItemClick = {it ->
            // pass only the common name, so the fragment can look up all data using the key
            val uuid = it.UniqueId
            val action = ObservationsFragmentDirections.actionObservationsFragmentToObservationDetailFragment(uuid)

            view.findNavController().navigate(action)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_observations, container, false)
    }

}