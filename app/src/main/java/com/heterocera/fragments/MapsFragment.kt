package com.heterocera.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.heterocera.R
import com.heterocera.adapters.HeteroceraInfoWindowAdapter
import com.heterocera.database.HeteroceraDatabase
import com.heterocera.database.Repository
import com.heterocera.viewmodel.ObservationViewModel
import com.heterocera.viewmodel.ObservationViewModelFactory
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MapsFragment : Fragment(){
    private lateinit var theMap: GoogleMap
    private lateinit var activityContext: Context

    val database by lazy { HeteroceraDatabase.getDatabase(activityContext)}
    private val repository
    by lazy { Repository(database.encyclopediaDAO(),database.observationDAO()) }
    private val observationViewModel: ObservationViewModel by viewModels {
        ObservationViewModelFactory(repository)
    }
    private val callback = OnMapReadyCallback { googleMap ->
        theMap = googleMap

        // pack local observations onto map
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            repository.observationsList.collect{
                for (obs in it){
                    addRecordingLocation(obs.ObservationLat,obs.ObservationLng,obs.DateTimestamp,
                        obs.UniqueId,obs.ObservationImageLink,obs.ObservationSpeciesCommonName,
                        description = obs.ObservationDescription)

                }

            }
        }
        theMap.setInfoWindowAdapter(HeteroceraInfoWindowAdapter(activityContext))
    }


    private fun addRecordingLocation(
        latitude: String?, longitude: String?, datetime: String?,
        uuid: String?, markerImage: String?, species:String?,description:String?)
    {
        val latLng = LatLng(latitude!!.toDouble(),longitude!!.toDouble())
        val instant = Instant.ofEpochMilli(datetime!!.toLong())
        val dateTimeStamp = OffsetDateTime.ofInstant(instant, ZoneId.of("GMT+1"))
            .format(DateTimeFormatter.ISO_DATE_TIME).toString()
        val snippetString = StringBuilder().append("Latitude: ").append(latitude.toString())
            .append("Longitude: ").append(longitude.toString()).append("\nOn: ")
            .append(dateTimeStamp).append("\n").append(description.toString()).toString()
        val newMarker = theMap.addMarker(MarkerOptions().position(latLng).snippet(snippetString)
            .title(species))

        val inputStream = activityContext.contentResolver.openInputStream(Uri.parse(markerImage))
        val imgBitmap = BitmapFactory.decodeStream(inputStream)
        newMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(imgBitmap.scale(75,75)))
        newMarker.tag =  uuid
        newMarker.hideInfoWindow()
        newMarker.showInfoWindow()
        inputStream!!.close()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activityContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // the centre of ireland - for a definition of centre
        val ireland = LatLng(53.457658982613474, -7.897355428622972)
        // create bounding box for latitude and longitude of ireland (rough)
        // this technically includes the beginning of orkney but
        // it'll do
        val bounds = LatLngBounds.Builder()
            .include(LatLng(55.24730233538849, -5.302644556426929))
            .include(LatLng(51.31848471945964, -10.65847903879891)).build()
        val mapOptions = GoogleMapOptions()
        mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL).zoomControlsEnabled(true).camera(
            CameraPosition(ireland,6.65F,0F,0F)).latLngBoundsForCameraTarget(bounds)
        val mapFragment = SupportMapFragment.newInstance(mapOptions)
        // make the actual map fragment
        childFragmentManager.beginTransaction().add(R.id.map,mapFragment).commit()
        mapFragment.getMapAsync(callback)


    }

}
