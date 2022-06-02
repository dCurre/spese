package com.dcurreli.spese.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()
    private val _userLiveData = MutableLiveData<User>()
    private val _userListLiveData = MutableLiveData<List<User>>()
    val userLiveData: LiveData<User> = _userLiveData
    val userListLiveData: LiveData<List<User>> = _userListLiveData

    fun findAll() {
        viewModelScope.launch {
            _userListLiveData.value = repository.findAll()
        }
    }

    fun findById(uid : String) {
        viewModelScope.launch {
            _userLiveData.value = repository.findByID(uid)
        }
    }

    fun findByIdList(uidList : List<String>) {
        viewModelScope.launch {
            _userListLiveData.value = repository.findAllByIdList(uidList)
        }
    }

    fun insert(user: User) {
        repository.insert(user)
    }

    fun update(id: String, user: User) {
        viewModelScope.launch {
            _userLiveData.value = repository.update(id, user)
        }
    }

    fun updateByField(id: String, field: String, value : Any) {
        viewModelScope.launch {
            _userLiveData.value = repository.updateByField(id, field, value)
        }
    }
}