package com.heterocera.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Observations(

    @NonNull @PrimaryKey @ColumnInfo(name = "unique_uuid") val UniqueId: String,
    @ColumnInfo(name = "observation_timestamp") val DateTimestamp: String?,
    @ColumnInfo(name = "observation_latitude")val ObservationLat: String?,
    @ColumnInfo(name = "observation_longitude")val ObservationLng: String?,
    @ColumnInfo(name = "observation_description")val ObservationDescription: String?,
    @ColumnInfo(name="observation_formal_name") val ObservationSpeciesFormalName:String?,
    @ColumnInfo(name="observation_common_name") val ObservationSpeciesCommonName:String?,
    @ColumnInfo(name="observation_image_link") val ObservationImageLink:String?
)
