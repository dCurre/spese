package com.dcapps.spese.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dcapps.spese.data.entities.Expense
import com.dcapps.spese.data.repositories.ExpenseRepository

class ExpenseViewModel : ViewModel() {

    private val repository = ExpenseRepository()
    private val _expenseLiveData = MutableLiveData<Expense>()
    private val _expenseListLiveData = MutableLiveData<List<Expense>>()
    val expenseLiveData: LiveData<Expense> = _expenseLiveData
    val expenseListLiveData: LiveData<List<Expense>> = _expenseListLiveData

    fun findAll() {
        repository.findAll(_expenseListLiveData)
    }

    fun findByID(id : String){
        repository.findByID(id, _expenseLiveData)
    }

    fun findAllByExpensesListID(id : String) {
        repository.findAllByExpensesListID(id, _expenseListLiveData)
    }

    fun insert(expense: Expense) {
        repository.insert(expense)
    }

    fun update(id: String, updateMap: HashMap<String, Any>) {
        repository.update(id, updateMap)
    }

    fun delete(id: String) {
        repository.delete(id)
    }

    fun deleteList(expenseList: List<Expense>) {
        repository.deleteList(expenseList)
    }
}