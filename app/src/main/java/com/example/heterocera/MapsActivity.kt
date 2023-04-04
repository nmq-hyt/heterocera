package com.example.heterocera

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import java.util.UUID

class MapsActivity : AppCompatActivity(),GoogleMap.OnMarkerClickListener, OnMapReadyCallback, OnMapsSdkInitializedCallback {
    private lateinit var theMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_fragment_layout)
        MapsInitializer.initialize(this.applicationContext,
            MapsInitializer.Renderer.LATEST,this)
        if (savedInstanceState == null) {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
        supportFragmentManager.setFragmentResultListener("mapActivityRequestKey",
            this) { request,bundle ->
                addRecordingLocation(bundle.getDouble("observationLatitude"),
                bundle.getDouble("observationLongitude"),bundle.getString("observationTimeStamp")
                ,bundle.getString("UUID"),bundle.getParcelable("image"))

            }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        theMap = googleMap
        val ireland = LatLng(53.1424,  7.6921)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ireland))

    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }

    private fun addRecordingLocation(latitude : Double, longitude : Double, datetime : String?,
                                      uuid: String?,markerImage: Bitmap?)
    {
        val latLng : LatLng = LatLng(latitude,longitude)
        val newMarker = theMap.addMarker(MarkerOptions().position(latLng).snippet(datetime)
            .icon(markerImage?.let { BitmapDescriptorFactory.fromBitmap(it) }))

        newMarker?.tag = uuid

    }

    override fun onMarkerClick(p0: Marker): Boolean {
        TODO("Not yet implemented")
    }


}