package com.heterocera

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.heterocera.database.HeteroceraDatabase
import com.heterocera.database.MothSpecies
import com.heterocera.database.ObservationDAO
import com.heterocera.database.Observations
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var observationDAO: ObservationDAO
    private lateinit var db: HeteroceraDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, HeteroceraDatabase::class.java).build()
        observationDAO = db.observationDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    suspend fun writeUserAndReadInList() {
        val observation:Observations = Observations("test","test","test"
        ,"test","test","test","test",
        "test")

        observationDAO.writeObservation("test","test","test"
            ,"test","test","test","test",
            "test")

      val testResult =  observationDAO.findByUUID("test")

        assertEquals(observation,testResult)
    }

    @Test
    @Throws(Exception::class)
    suspend fun ensureObservationsEmpty() {
        val observation:Observations = Observations("test","test","test"
            ,"test","test","test","test",
            "test")

        observationDAO.writeObservation("test","test","test"
            ,"test","test","test","test",
            "test")

        observationDAO.deleteAllObservations()
        var observationsResult: List<Observations>? = null
        var testList : List<Observations> = emptyList()
        observationDAO.getAll().collect {
            observationsResult = it
        }
        assertEquals(testList.isEmpty(), observationsResult!!.isEmpty())

    }

}
