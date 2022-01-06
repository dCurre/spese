package com.dcurreli.spese

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.utils.SpesaUtils
import com.google.firebase.database.DatabaseReference
import android.view.View as View1

class LoadSpeseFragment : Fragment(R.layout.load_spese) {

    private var _binding: LoadSpeseBinding? = null
    private lateinit var db: DatabaseReference
    private val TAG = javaClass.simpleName
    private lateinit var spesaArray : ArrayList<Spesa>
    private lateinit var dataForQuery : DataForQuery
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View1? {

        _binding = LoadSpeseBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recupero i dati da stampare per le spese
        if(arguments != null){
            dataForQuery = DataForQuery(arguments?.getString("startsAt")?.toDouble(), arguments?.getString("endsAt")?.toDouble())

            //Stampo la lista delle spese
            SpesaUtils.printSpesa(binding.listaSpese, requireContext(), dataForQuery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}