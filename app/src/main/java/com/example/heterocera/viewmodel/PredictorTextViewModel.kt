package com.example.heterocera.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class PredictedMoth (
    var predictedMothName: String? = null,
    var predictedMothConfidence: Float? = null
)

class PredictorTextViewModel : ViewModel() {
    private val _mutableMoths = MutableLiveData<List<PredictedMoth>>()
    val predictedMothSpecies: LiveData<List<PredictedMoth>> get() = _mutableMoths

    fun updatePredictions(predictedMoths: List<PredictedMoth>) {
        _mutableMoths.postValue(predictedMoths)
    }

}