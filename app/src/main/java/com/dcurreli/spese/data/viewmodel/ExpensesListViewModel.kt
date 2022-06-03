package com.dcurreli.spese.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dcurreli.spese.data.entity.ExpensesList
import com.dcurreli.spese.data.repository.ExpensesListRepository

class ExpensesListViewModel : ViewModel() {

    private val repository = ExpensesListRepository()
    private val _expensesListLiveData = MutableLiveData<ExpensesList>()
    private val _expensesListListLiveData = MutableLiveData<List<ExpensesList>>()
    val expensesListLiveData: LiveData<ExpensesList> = _expensesListLiveData
    val expensesListListLiveData: LiveData<List<ExpensesList>> = _expensesListListLiveData

    fun findAll() {
        repository.findAll(_expensesListListLiveData)
    }

    fun findAllByUserIDAndIsPaid(userID: String, hidePaidLists: Boolean) {
        repository.findAllByUserIDAndIsPaid(userID, hidePaidLists, _expensesListListLiveData)
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