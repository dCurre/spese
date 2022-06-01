package com.dcurreli.spese.main

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
import com.dcurreli.spese.adapters.ListaListeAdapter
import com.dcurreli.spese.data.viewmodel.ListaSpeseViewModel
import com.dcurreli.spese.data.viewmodel.UserViewModel
import com.dcurreli.spese.databinding.HomeFragmentBinding
import com.dcurreli.spese.utils.DBUtils
import com.squareup.picasso.Picasso


class HomeFragment : Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!
    private val user = DBUtils.getCurrentUser()!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HomeFragmentBinding.inflate(inflater, container, false)

        setupUserBar()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        printListaSpese()

        super.onViewCreated(view, savedInstanceState)

    }

    private fun printListaSpese() {
        val listaSpeseModel : ListaSpeseViewModel = ViewModelProvider(this)[ListaSpeseViewModel::class.java]
        val userModel : UserViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val listaSpeseAdapter = ListaListeAdapter(findNavController())
        val listePerRiga = 2 //NUMERO DI SPESE PER RIGA

        //Recupero listaSpese a partire dall'utente
        userModel.getUserById(user.uid)
        userModel.userLiveData.observe(viewLifecycleOwner) { user ->
           listaSpeseModel.findListsByUserID(user)
        }

        //Setup layout manager
        binding.listaSpese.layoutManager = GridLayoutManager(context, listePerRiga)
        binding.listaSpese.adapter = listaSpeseAdapter

        listaSpeseModel.listaSpeseListLiveData.observe(viewLifecycleOwner) { listaSpeseList ->
            listaSpeseAdapter.addItems(listaSpeseList)
        }
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

    @SuppressLint("SetTextI18n")
    private fun setupUserBar() {
        binding.userBarText.text = "Ciao,\n${user.displayName}"
        Picasso.get().load(user.photoUrl).into(binding.userBarImage)
    }

}