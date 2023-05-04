package com.heterocera.database

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class MothSpecies(
    @SuppressLint("KotlinNullnessAnnotation") @NonNull @PrimaryKey @ColumnInfo(name="formal_name") val SpeciesFormalName : String,
    @ColumnInfo(name = "common_name") val SpeciesVulgarName: String,
    @ColumnInfo(name = "description_string") val SpeciesDescription: String?,
    @ColumnInfo(name= "reference_image_link") val ReferencePhotographFileString : String?
    )
{
        override fun toString(): String {
            return SpeciesVulgarName
        }

}
