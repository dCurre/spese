package com.dcapps.spese.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dcapps.spese.data.entities.ExpensesList
import com.dcapps.spese.data.repositories.ExpensesListRepository

class ExpensesListViewModel : ViewModel() {

    private val repository = ExpensesListRepository()
    private val _expensesListLiveData = MutableLiveData<ExpensesList>()
    private val _expensesListsLiveData = MutableLiveData<List<ExpensesList>>()
    val expensesListLiveData: LiveData<ExpensesList?> = _expensesListLiveData
    val expensesListsLiveData: LiveData<List<ExpensesList>> = _expensesListsLiveData

    fun findAll() {
        repository.findAll(_expensesListsLiveData)
    }

    fun findAllByUserID(userID: String) {
        repository.findAllByUserID(userID, _expensesListsLiveData)
    }

    fun findAllByUserIDAndIsPaid(userID: String, hidePaidLists: Boolean) {
        repository.findAllByUserIDAndIsPaid(userID, hidePaidLists, _expensesListsLiveData)
    }

    fun findByID(id : String){
        repository.findByID(id, _expensesListLiveData)
    }

    fun insert(expensesList: ExpensesList) {
        repository.insert(expensesList)
    }

    fun update(id: String, updateMap: HashMap<String, Any>) {
        repository.update(id, updateMap)
    }

    fun updateByField(id: String, field: String, value : Any) {
        repository.updateByField(id, field, value)
    }

    fun delete(id : String){
        repository.delete(id)
    }
}