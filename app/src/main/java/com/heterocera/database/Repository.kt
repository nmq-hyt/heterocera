package com.heterocera.database

import kotlinx.coroutines.flow.Flow

class Repository(private val eDAO: EncyclopediaDAO, private val oDAO: ObservationDAO) {
    val observationsList: Flow<List<Observations>> = oDAO.getAll()
    val arrayMothSpecies: List<MothSpecies> = eDAO.getAll()
    val alphabetizedMothSpecies = eDAO.getAlphabetizedMoths()

    suspend fun writeObservation(obs_long:Double, obs_image_filename:String, obs_desc:String, obs_time:Long, uuid:String, obs_lat:Double,
                                 obs_formal:String, obs_comm:String) {
        oDAO.writeObservation(
            obs_long.toString(),obs_image_filename,obs_desc, obs_time.toString(),uuid, obs_lat.toString(),
            obs_formal,obs_comm)
    }

    suspend fun updateObservation(formal_name:String,common_name:String,description:String,observation_uuid:String) {
        oDAO.updateObservation(formal_name,common_name,description,observation_uuid)
    }

    suspend fun updateDescription(description: String,uuid:String) {
        oDAO.updateDescription(description,uuid)
    }

    suspend fun deleteAllObservations() {
        oDAO.deleteAllObservations()
    }

    suspend fun getObservation(uuid: String): Observations {
        return oDAO.findByUUID(uuid)
    }
    fun getFormalFromCommon(commonName: String):String {
        return eDAO.getFormalFromCommon(commonName)
    }

    suspend fun getMothFromFormal(formalName: String): MothSpecies {
        return eDAO.findByFormalName(formalName)
    }


    suspend fun getMothDescription(formalName:String) : String {
        return eDAO.getMothDesc(formalName)
    }
    suspend fun getAllMoths(): List<MothSpecies> {
        return eDAO.getAll()
    }


}