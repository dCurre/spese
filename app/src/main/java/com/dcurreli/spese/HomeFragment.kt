package com.dcurreli.spese

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.databinding.ActivityMainBinding
import com.dcurreli.spese.databinding.HomeFragmentBinding
import com.dcurreli.spese.objects.Mese
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private lateinit var bindingMainActivity: ActivityMainBinding
    private lateinit var db: DatabaseReference
    private val TAG = javaClass.simpleName
    private lateinit var meseArray : ArrayList<Mese>

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

        //Recupero i dati da stampare per le spese
        //dataForQuery = (getActivity() as MainActivity).getDataForQuery()
        db = Firebase.database.reference.child("mese")
        meseArray = ArrayList()

        //Stampo la lista mesi
        //MeseUtils.printMese(db, bindingMainActivity.listaMese, requireContext(), meseArray)

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