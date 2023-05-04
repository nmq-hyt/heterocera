package com.heterocera.adapters

import android.content.Context
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.layout.Layout
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.heterocera.R

class HeteroceraInfoWindowAdapter(mContext: Context): GoogleMap.InfoWindowAdapter {
    private var infoWindow : View = LayoutInflater
        .from(mContext).inflate(R.layout.heterocera_info_window,null)

    override fun getInfoContents(p0: Marker): View? {
        infoWindow.findViewById<TextView>(R.id.marker_title).text = p0.title
        infoWindow.findViewById<TextView>(R.id.marker_description).text = p0.snippet
        return infoWindow
    }

    override fun getInfoWindow(p0: Marker): View? {
        infoWindow.findViewById<TextView>(R.id.marker_title).text = p0.title
        infoWindow.findViewById<TextView>(R.id.marker_description).text = p0.snippet

        return infoWindow
        }
}