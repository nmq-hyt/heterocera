package com.example.heterocera.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class Repository(private val eDAO: EncyclopediaDAO, private val oDAO: ObservationDAO) {
    val allObservations: Flow<List<Observations>> = oDAO.getAll()
    val arrayMothSpecies = eDAO.getAll()
    val observationsList = oDAO.getAllList()
    val alphabetizedMothSpecies = eDAO.getAlphabetizedMoths()

    fun writeObservation(obs_long:Double,obs_desc:String,obs_time:Long,uuid:String,obs_lat:Double,
                         obs_formal:String,obs_comm:String) {
        oDAO.writeObservation(
            obs_long.toString(),obs_desc, obs_time.toString(),uuid, obs_lat.toString(),
            obs_formal,obs_comm)
    }
    fun getFormalFromCommon(commonName: String) {
        eDAO.getFormalFromCommon(commonName)
    }

    fun getMothDescription(formalName:String) {
        eDAO.getMothDesc(formalName)
    }
    fun getAllMoths() {
        eDAO.getAll()
    }


}