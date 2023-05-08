package com.heterocera.fragments

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.heterocera.R
import com.heterocera.database.HeteroceraDatabase
import com.heterocera.database.MothSpecies
import com.heterocera.database.Repository
import com.heterocera.viewmodel.MothViewModel
import com.heterocera.viewmodel.MothViewModelFactory
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class ObservationDetailFragment : Fragment() {
    private lateinit var activityContext: Context
    val database by lazy { HeteroceraDatabase.getDatabase(activityContext)}
    private val repository by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    private val mothViewModel: MothViewModel by viewModels {
        MothViewModelFactory(repository)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
        activityContext.theme.applyStyle(R.style.Theme_Heterocera,true)

    }

    // create the overall UI elements
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_observation_record, container, false)
    }

    // the function designed to handle view
    // and as such is overridden to do exactly that;
    // setup the UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uuid = arguments?.getString("uuid")
        // for asynchronous actions, like retrieving data
        // Room mandates that you can't do database operations on the main thread
        // which is eminently reasonable in my mind
        // the database reads and assignments to UI
        // is done at lifecycle creation, after the UI
        // has been initialized in onCreateView
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val observation = uuid?.let { repository.getObservation(it) }
            val observationCommonName = view.findViewById<TextView>(R.id.observation_moth_common_name)
            val observationDescription = view.findViewById<EditText>(R.id.observation_description)

            class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
                // this is a private class designed to tell what the spinner item
                // the choice of moth in the observation to do
                override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                    val observationSpeciesNameString = parent.getItemAtPosition(pos).toString()
                    runBlocking {
                        val newObservationMothSpecies =
                            repository.getMothFromFormal(repository.getFormalFromCommon(observationSpeciesNameString))
                        val mothSpeciesText = StringBuilder(newObservationMothSpecies.SpeciesVulgarName)
                            .append("\n").append("\n").append(newObservationMothSpecies.SpeciesFormalName)
                            observationCommonName.text = mothSpeciesText
                        repository.updateObservation(newObservationMothSpecies.SpeciesFormalName,
                            newObservationMothSpecies.SpeciesVulgarName,
                            observationDescription.text.toString(),
                            uuid.toString()
                        )
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
            val spinner : Spinner = view.findViewById(R.id.observation_common_name_spinner)
            val mothSpeciesListAdapter = ArrayAdapter(activityContext,R.layout.moth_spinner_item,
                mothViewModel.mothArray).also {adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            spinner.adapter = mothSpeciesListAdapter
            spinner.onItemSelectedListener = SpinnerActivity()

            if (observation != null) {
                val obsDetailImageView = view.findViewById<ImageView>(R.id.observation_detail_image)
                Glide.with(view).clear(obsDetailImageView)
                Glide.with(view).load(Uri.parse(observation.ObservationImageLink))
                    .into(obsDetailImageView)
                val mothSpeciesText = StringBuilder(observation.ObservationSpeciesFormalName.toString())
                    .append("\n").append("\n").append(observation.ObservationSpeciesFormalName)
                observationCommonName.text = mothSpeciesText
                val instant = Instant.ofEpochMilli(observation.DateTimestamp!!.toLong())
                val dateTimeStamp = OffsetDateTime.ofInstant(instant, ZoneId.of("GMT+1"))
                    .format(DateTimeFormatter.ISO_DATE_TIME).toString()
                view.findViewById<TextView>(R.id.observation_gps_text_view).text =
                    buildString {
                        this.append("Latitude: ").append(observation.ObservationLat)
                            .append(" Longitude: ").append(observation.ObservationLat)
                            .append(" \nOn: ").append(dateTimeStamp)
                    }
                observationDescription.text = SpannableStringBuilder(observation.ObservationDescription)
                observationDescription.doOnTextChanged { text, start, before, count ->
                    runBlocking {
                        repository.updateDescription(text.toString(),uuid)
                    }
                }

            }
        }

    }

}