package com.dcurreli.spese.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dcurreli.spese.data.entity.ListaSpese
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.repository.ListaSpeseRepository

class ListaSpeseViewModel : ViewModel() {

    private val repository = ListaSpeseRepository()

    private val _listaSpeseListLiveData = MutableLiveData<List<ListaSpese>>()
    val listaSpeseListLiveData: LiveData<List<ListaSpese>> = _listaSpeseListLiveData
    private val _listaSpeseLiveData = MutableLiveData<ListaSpese>()
    val listaSpeseLiveData: LiveData<ListaSpese> = _listaSpeseLiveData

    fun findAll() {
        repository.findAll(_listaSpeseListLiveData)
    }

    fun findByUserID(user: User) {
        repository.findByUserID(_listaSpeseListLiveData, user)
    }

    fun findById(id : String){
        repository.findById(_listaSpeseLiveData, id)
    }

    fun update(id: String, listaSpese: ListaSpese) {
        repository.update(id, listaSpese)
    }

    fun updateByField(id: String, field: String, value : Any) {
        repository.updateByField(id, field, value)
    }

    fun insert(listaSpese: ListaSpese) {
        repository.insert(listaSpese)
    }

    fun delete(id : String){
        repository.delete(id)
    }
}