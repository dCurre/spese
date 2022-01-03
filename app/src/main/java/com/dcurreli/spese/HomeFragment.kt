package com.dcurreli.spese

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.databinding.HomeFragmentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference




class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private lateinit var db: DatabaseReference
    private var queryRes: DataSnapshot? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addSpesaButton.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_AddSpesaFragment)
        }
        binding.loadSpeseButton.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_loadSpeseFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}