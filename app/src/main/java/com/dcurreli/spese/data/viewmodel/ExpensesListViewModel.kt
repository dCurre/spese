package com.dcurreli.spese.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dcurreli.spese.data.entity.ExpensesList
import com.dcurreli.spese.data.repository.ExpensesListRepository
import kotlinx.coroutines.launch

class ExpensesListViewModel : ViewModel() {

    private val repository = ExpensesListRepository()
    private val _expensesListListLiveData = MutableLiveData<List<ExpensesList>>()
    val expensesListListLiveData: LiveData<List<ExpensesList>> = _expensesListListLiveData
    private val _expensesListLiveData = MutableLiveData<ExpensesList>()
    val expensesListLiveData: LiveData<ExpensesList> = _expensesListLiveData

    fun findAll() {
        viewModelScope.launch {
            _expensesListListLiveData.value = repository.findAll()
        }
    }

    fun findByUserIDAndIsPaid(userID: String, hidePaidLists: Boolean) {
        viewModelScope.launch {
            _expensesListListLiveData.value = repository.findByUserIDAndIsPaid(userID, hidePaidLists)
        }
    }

    fun findById(id : String){
        viewModelScope.launch {
            _expensesListLiveData.value = repository.findByID(id)
        }
    }

    fun insert(expensesList: ExpensesList) {
        repository.insert(expensesList)
    }

    fun update(id: String, expensesList: ExpensesList) {
        viewModelScope.launch {
            _expensesListLiveData.value = repository.update(id, expensesList)
        }
    }

    fun updateByField(id: String, field: String, value : Any) {
        viewModelScope.launch {
            _expensesListLiveData.value = repository.updateByField(id, field, value)
        }
    }

    fun delete(id : String){
        repository.delete(id)
    }
}