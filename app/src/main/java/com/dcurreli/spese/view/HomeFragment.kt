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
import com.dcurreli.spese.data.viewmodel.ListaSpeseViewModel
import com.dcurreli.spese.data.viewmodel.UserViewModel
import com.dcurreli.spese.databinding.HomeFragmentBinding
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.SpesaUtils
import com.squareup.picasso.Picasso


class HomeFragment : Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!
    private val user = DBUtils.getLoggedUser()
    private lateinit var userModel : UserViewModel
    private lateinit var listaSpeseModel : ListaSpeseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        userModel = ViewModelProvider(this)[UserViewModel::class.java]
        listaSpeseModel = ViewModelProvider(this)[ListaSpeseViewModel::class.java]

        SpesaUtils.exportDataToFirestore(this, viewLifecycleOwner)

        printListaSpese()

        setupUserBar()

        return binding.root
    }

    private fun printListaSpese() {
        val listaSpeseAdapter = ListaSpeseAdapter(findNavController())
        val listsPerRow = 2

        //Recupero listaSpese a partire dall'utente
        userModel.getById(user.uid)
        userModel.userLiveData.observe(viewLifecycleOwner) { user ->
           listaSpeseModel.findByUserID(user)
        }

        //Aggiungo le liste estratte all'adapter
        listaSpeseModel.listaSpeseListLiveData.observe(viewLifecycleOwner) { listaSpeseList ->
            if (listaSpeseList != null) {
                listaSpeseAdapter.addItems(listaSpeseList)
            }
        }

        //Setup griglia liste
        binding.listaSpese.layoutManager = GridLayoutManager(context, listsPerRow)
        binding.listaSpese.adapter = listaSpeseAdapter

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