package com.example.heterocera.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.heterocera.database.MothSpecies
import com.example.heterocera.database.Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MothViewModel(private val repository: Repository) : ViewModel() {
    val mothArray : List<MothSpecies> = repository.arrayMothSpecies
    val alphabetizedMothSpecies = repository.alphabetizedMothSpecies


}

class MothViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MothViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MothViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }

}