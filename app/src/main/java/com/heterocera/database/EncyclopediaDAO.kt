package com.heterocera.database


import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EncyclopediaDAO {
    @Query("SELECT * FROM MothSpecies")
    fun getAll(): List<MothSpecies>

    @Query("SELECT * FROM MothSpecies WHERE common_name LIKE :argument LIMIT 1")
    suspend fun findByCommonName(argument:String): MothSpecies

    @Query("SELECT * FROM MothSpecies WHERE formal_name LIKE :argument LIMIT 1")
    suspend fun findByFormalName(argument:String): MothSpecies

    @Query("SELECT * FROM MothSpecies ORDER BY common_name ASC")
    fun getAlphabetizedMoths(): Flow<List<MothSpecies>>

    @Query("SELECT description_string FROM MothSpecies WHERE formal_name LIKE :argument LIMIT 1")
    fun getMothDesc(argument: String) : String

    @Query("SELECT formal_name FROM MothSpecies WHERE common_name LIKE :argument LIMIT 1")
    fun getFormalFromCommon(argument: String): String
}