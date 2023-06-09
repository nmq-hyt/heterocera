package com.heterocera.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MothSpecies::class, Observations::class ], version = 1, exportSchema = true)
abstract class HeteroceraDatabase : RoomDatabase() {
    abstract fun encyclopediaDAO(): EncyclopediaDAO
    abstract fun observationDAO(): ObservationDAO


    companion object {
        //singleton

        @Volatile
        private var INSTANCE: HeteroceraDatabase? = null
        fun getDatabase(context: Context): HeteroceraDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HeteroceraDatabase::class.java,
                    "moth_database"
                ).createFromAsset("heterocera_database.db")
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }



}
