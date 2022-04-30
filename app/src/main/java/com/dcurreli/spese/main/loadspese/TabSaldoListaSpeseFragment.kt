package com.dcurreli.spese.main.loadspese

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.LoadSpeseTabSaldoBinding
import com.dcurreli.spese.utils.SpesaUtils
import android.view.View as View1

class TabSaldoListaSpeseFragment() : Fragment(R.layout.load_spese_tab_saldo) {

    companion object {
        fun newInstance(args: Bundle?): TabSaldoListaSpeseFragment{
            val fragment = TabSaldoListaSpeseFragment().apply{
                arguments =  args
            }
            return fragment
        }
    }

    private var _binding: LoadSpeseTabSaldoBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = LoadSpeseTabSaldoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Stampo le spese
        SpesaUtils.printSaldo(
            binding,
            requireContext(),
            idListaSpese = arguments?.getString("idLista").toString() //id lista
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}