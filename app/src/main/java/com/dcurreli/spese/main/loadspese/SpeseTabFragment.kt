package com.dcurreli.spese.main.loadspese

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.LoadSpeseTabSpeseBinding
import com.dcurreli.spese.utils.SpesaUtils
import android.view.View as View1

class SpeseTabFragment() : Fragment(R.layout.load_spese_tab_spese) {

    companion object {
        const val DATA_KEY= "name"

        fun newInstance(args: Bundle?): SpeseTabFragment{
            val fragment = SpeseTabFragment().apply{
                arguments =  args
            }
            return fragment
        }
    }

    private var _binding: LoadSpeseTabSpeseBinding? = null
    private val TAG = javaClass.simpleName
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = LoadSpeseTabSpeseBinding.inflate(inflater, container, false)



        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, ">>DAVIDE ${arguments?.getString("idLista").toString()}")

        //Stampo le spese
        SpesaUtils.printSpese(
            binding,
            requireContext(),
            idListaSpese = arguments?.getString("idLista").toString(), //id lista
            activity
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}