package com.dcurreli.spese.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.ListaSpeseAdapter
import com.dcurreli.spese.data.viewmodel.ExpensesListViewModel
import com.dcurreli.spese.data.viewmodel.UserViewModel
import com.dcurreli.spese.databinding.HomeFragmentBinding
import com.dcurreli.spese.utils.DBUtils
import com.squareup.picasso.Picasso


class HomeFragment : Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!
    private val loggedUser = DBUtils.getLoggedUser()
    private lateinit var userModel : UserViewModel
    private lateinit var listaSpeseModel : ExpensesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        userModel = ViewModelProvider(this)[UserViewModel::class.java]
        listaSpeseModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]

        //SpesaUtils.exportDataToFirestore(this, viewLifecycleOwner)

        printListaSpese()

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

    private fun printListaSpese() {
        val listaSpeseAdapter = ListaSpeseAdapter(findNavController())
        val listsPerRow = 2

        userModel.findById(loggedUser.uid)
        userModel.userLiveData.observe(viewLifecycleOwner) { user ->
            listaSpeseModel.findByUserIDAndIsPaid(user.id, user.hidePaidLists)
        }

        //Recupero listaSpese a partire dall'utente
        listaSpeseModel.expensesListListLiveData.observe(viewLifecycleOwner) { expensesListList ->
            listaSpeseAdapter.addItems(expensesListList)
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