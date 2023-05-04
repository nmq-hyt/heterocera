package com.heterocera.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.heterocera.R
import com.heterocera.database.HeteroceraDatabase
import com.heterocera.database.Repository
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class ObservationDetailFragment : Fragment() {
    private lateinit var activityContext: Context
    val database by lazy { HeteroceraDatabase.getDatabase(activityContext)}
    private val repository by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    
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
        // for asynchronous actions, like retreiving data
        // Room mandates that you can't do database operations on the main thread
        // which is eminetly reasonable in y mind
        // the database reads and assignments to UI
        // is done at lifecycle creation, after the UI
        // has been initialized in onCreateView
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val observation = uuid?.let { repository.getObservation(it) }
            if (observation != null) {
                val obsDetailImageView = view.findViewById<ImageView>(R.id.observation_detail_image)
                Glide.with(view).clear(obsDetailImageView)
                Glide.with(view).load(Uri.parse(observation.ObservationImageLink))
                    .into(obsDetailImageView)
                view.findViewById<TextView>(R.id.observation_moth_common_name).text =
                    observation.ObservationSpeciesCommonName
                view.findViewById<TextView>(R.id.observation_formal_view)
                    .text = observation.ObservationSpeciesFormalName
                val instant = Instant.ofEpochMilli(observation.DateTimestamp!!.toLong())
                val dateTimeStamp = OffsetDateTime.ofInstant(instant, ZoneId.of("GMT+1"))
                    .format(DateTimeFormatter.ISO_DATE_TIME).toString()
                view.findViewById<TextView>(R.id.observation_gps_text_view).text =
                    buildString {
                        this.append("Latitude: ").append(observation.ObservationLat)
                            .append(" Longitude: ").append(observation.ObservationLat)
                            .append(" \nOn: ").append(dateTimeStamp)
                    }
                view.findViewById<TextView>(R.id.observation_description)
                    .text = observation.ObservationDescription

            }
        }

    }

}