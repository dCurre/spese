package com.dcurreli.spese.main.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.JoinFragmentBinding
import com.dcurreli.spese.main.MainActivity


class JoinFragment : Fragment(R.layout.join_fragment) {

    private var _binding: JoinFragmentBinding? = null
    private val TAG = javaClass.simpleName

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = JoinFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadJoinGroupDetails()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadJoinGroupDetails(){
        //Cambio il titolo della lista
        val toolbar = (activity as MainActivity).supportActionBar
        if (arguments != null) { toolbar?.title = "Join ${arguments?.getString("idLista")}" }

        binding.textViewProva.text = arguments?.getString("idLista").toString()
    }

}