package com.dcapps.spese.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dcapps.spese.R
import com.dcapps.spese.adapters.ListaSpeseAdapter
import com.dcapps.spese.data.viewmodel.ExpensesListViewModel
import com.dcapps.spese.data.viewmodel.UserViewModel
import com.dcapps.spese.databinding.HomeFragmentBinding
import com.dcapps.spese.utils.DBUtils
import com.squareup.picasso.Picasso


class HomeFragment : Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!
    private val loggedUser = DBUtils.getLoggedUser()
    private lateinit var userViewModel : UserViewModel
    private lateinit var expensesListViewModel : ExpensesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        expensesListViewModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]

        printExpensesLists()

        setupUserBar()

        return binding.root
    }

    override fun onResume() {
        //Faccio riapparire la bottom nav
        (activity as MainActivity).setBottomNavVisibility(true)
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun printExpensesLists() {
        val listaSpeseAdapter = ListaSpeseAdapter(findNavController())
        val listsPerRow = 2

        userViewModel.findByID(loggedUser.uid)
        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            expensesListViewModel.findAllByUserIDAndIsPaid(user.id, user.hidePaidLists)
        }

        //Recupero listaSpese a partire dall'utente
        expensesListViewModel.expensesListsLiveData.observe(viewLifecycleOwner) { expensesLists ->
            listaSpeseAdapter.addItems(expensesLists)
        }

        //Setup griglia liste
        binding.listaSpese.layoutManager = GridLayoutManager(context, listsPerRow)
        binding.listaSpese.adapter = listaSpeseAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setupUserBar() {
        binding.userBarText.text = "Ciao,\n${loggedUser.displayName}"
        Picasso.get().load(loggedUser.photoUrl).into(binding.userBarImage)
    }

}