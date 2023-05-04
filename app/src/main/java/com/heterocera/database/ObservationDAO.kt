package com.heterocera.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDAO {
    @Query("SELECT * FROM Observations")
    fun getAll(): Flow<List<Observations>>

    @Query("SELECT * FROM Observations WHERE unique_uuid LIKE :argument LIMIT 1")
    suspend fun findByUUID(argument:String): Observations


    @Query("INSERT INTO Observations (observation_longitude,observation_image_link,observation_description,observation_timestamp,unique_uuid,observation_latitude,observation_formal_name,observation_common_name) VALUES (:obs_long,:obs_image_filename,:obs_desc,:obs_time,:uuid,:obs_lat,:obs_formal,:obs_comm)")
    suspend fun writeObservation(obs_long:String,obs_image_filename:String,obs_desc:String,obs_time:String,uuid:String,obs_lat:String,
                        obs_formal:String,obs_comm:String)

    @Query("DELETE FROM Observations")
    suspend fun deleteAllObservations()
}