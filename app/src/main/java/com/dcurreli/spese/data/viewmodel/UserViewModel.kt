package com.dcurreli.spese.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.repository.UtenteRepository

class UserViewModel : ViewModel() {

    private val repository = UtenteRepository()

    private val _userListLiveData = MutableLiveData<List<User>>()
    val userListLiveData: LiveData<List<User>> = _userListLiveData
    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User> = _userLiveData

    fun findAll() {
        repository.getAll(_userListLiveData)
    }

    fun getUserById(uid : String) {
        repository.getUserById(_userLiveData, uid)
    }

    fun getUserListByIdList(uidList : List<String>) {
        repository.getUserListByIdList(_userListLiveData, uidList)
    }

    fun update(id: String, user: User) {
        repository.update(id, user)
    }

    fun updateByField(id: String, field: String, value : Any) {
        repository.updateByField(id, field, value)
    }
}