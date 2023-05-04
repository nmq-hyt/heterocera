package com.heterocera.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heterocera.database.MothSpecies
import com.heterocera.database.Repository

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