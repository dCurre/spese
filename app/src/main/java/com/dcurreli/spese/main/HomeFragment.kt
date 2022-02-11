package com.dcurreli.spese.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.HomeFragmentBinding
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.ListaSpeseUtils
import com.squareup.picasso.Picasso


class HomeFragment : Fragment(R.layout.home_fragment) {

    private var _binding: HomeFragmentBinding? = null
    private val TAG = javaClass.simpleName
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserBar()
        ListaSpeseUtils.printListe(requireContext(), binding, findNavController())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun setupUserBar() {
        val user = DBUtils.getCurrentUser()

        binding.userBarText.text = "Benvenuto,\n${user?.displayName}"
        Picasso.get().load(user?.photoUrl).into(binding.userBarImage)
        binding.userBarSettings.setOnClickListener {
            findNavController().navigate(R.id.action_To_SettingsFragment)
        }
    }
}