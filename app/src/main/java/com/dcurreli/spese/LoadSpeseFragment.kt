package com.dcurreli.spese

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.view.View as View1

class LoadSpeseFragment : Fragment() {

    private var _binding: LoadSpeseBinding? = null
    private val db = Firebase.firestore
    private val path = "spese"


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1? {

        _binding = LoadSpeseBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spese = db.collection(path)

        binding.backHomeButton.setOnClickListener {
            findNavController().navigate(R.id.action_loadSpeseFragment_to_HomeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}