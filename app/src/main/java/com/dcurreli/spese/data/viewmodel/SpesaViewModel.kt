package com.dcurreli.spese.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dcurreli.spese.data.repository.SpesaRepository
import com.dcurreli.spese.objects.Spesa

class SpesaViewModel : ViewModel() {

    private val repository = SpesaRepository()

    private val _spesaListLiveData = MutableLiveData<List<Spesa>>()
    val spesaListLiveData: LiveData<List<Spesa>> = _spesaListLiveData
    private val _spesaLiveData = MutableLiveData<Spesa>()
    val spesaLiveData: LiveData<Spesa> = _spesaLiveData

    fun findAll() {
        repository.getAll(_spesaListLiveData)
    }

    fun findByListaSpesaID(id : String) {
        repository.findByListaSpesaID(_spesaListLiveData, id)
    }

    fun update(spesa: Spesa) {
        repository.update(spesa)
    }

    fun insert(spesa: Spesa) {
        repository.insert(spesa)
    }

    fun delete(id: String) {
        repository.delete(id)
    }

    fun deleteList(spesaList: List<Spesa>) {
        repository.deleteList(spesaList)
    }
}