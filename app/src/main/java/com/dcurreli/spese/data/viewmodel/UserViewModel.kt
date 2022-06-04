package com.dcurreli.spese.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dcurreli.spese.data.entity.User
import com.dcurreli.spese.data.repository.UserRepository

class UserViewModel : ViewModel() {

    private val repository = UserRepository()
    private val _userLiveData = MutableLiveData<User>()
    private val _userListLiveData = MutableLiveData<List<User>>()
    val userLiveData: LiveData<User> = _userLiveData
    val userListLiveData: LiveData<List<User>> = _userListLiveData

    fun findAll() {
        repository.findAll(_userListLiveData)
    }

    fun findAllByUserIdList(uidList : List<String>) {
        repository.findAllByIdList(uidList, _userListLiveData)
    }

    fun findByID(id : String) {
        repository.findByID(id, _userLiveData)
    }

    fun insert(user: User) {
        repository.insert(user)
    }

    fun update(id: String, updateMap: HashMap<String, Any>) {
        repository.update(id, updateMap)
    }

    fun updateByField(id: String, field: String, value : Any) {
        repository.updateByField(id, field, value)
    }
}