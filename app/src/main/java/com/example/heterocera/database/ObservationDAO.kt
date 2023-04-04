package com.example.heterocera.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDAO {
    @Query("SELECT * FROM Observations")
    fun getAll(): Flow<List<Observations>>

    @Query("SELECT * FROM Observations WHERE observation_common_name LIKE :argument LIMIT 1")
    fun findByCommonName(argument:String): Observations

    @Query("SELECT * FROM Observations")
    fun getAllList(): List<Observations>

    @Query("INSERT INTO Observations (observation_longitude,observation_description,observation_timestamp,unique_uuid,observation_latitude,observation_formal_name,observation_common_name) VALUES (:obs_long,:obs_desc,:obs_time,:uuid,:obs_lat,:obs_formal,:obs_comm)")
    fun writeObservation(obs_long:String,obs_desc:String,obs_time:String,uuid:String,obs_lat:String,
                        obs_formal:String,obs_comm:String)
}