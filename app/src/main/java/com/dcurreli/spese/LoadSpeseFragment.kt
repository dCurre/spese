package com.dcurreli.spese

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.dcurreli.spese.databinding.SpeseListBinding
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.utils.SpesaUtils
import android.view.View as View1

class LoadSpeseFragment : Fragment(R.layout.load_spese) {

    private var _binding: LoadSpeseBinding? = null
    private val TAG = javaClass.simpleName
    private lateinit var dataForQuery : DataForQuery
    private val binding get() = _binding!!

    private var _bindingSpeseList: SpeseListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View1? {

        _binding = LoadSpeseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recupero i dati da stampare per le spese (dal main activity)
        if(arguments != null){
            dataForQuery = DataForQuery(arguments?.getString("startsAt")?.toDouble(), arguments?.getString("endsAt")?.toDouble())

            //Stampo la lista delle spese
            SpesaUtils.printSpesa(binding, requireContext(), dataForQuery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}