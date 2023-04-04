package com.example.heterocera.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.heterocera.database.Observations
import com.example.heterocera.database.Repository

class ObservationViewModel(private val repository: Repository): ViewModel() {
    val allObservations : LiveData<List<Observations>> = repository.allObservations.asLiveData()
    val observationsArray = repository.observationsList
}

class ObservationViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MothViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ObservationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }

}