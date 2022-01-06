package com.dcurreli.spese

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.utils.MeseUtils
import com.dcurreli.spese.utils.SpesaUtils
import org.jetbrains.anko.imageResource
import android.view.View as View1








class LoadSpeseFragment : Fragment(R.layout.load_spese) {

    private var _binding: LoadSpeseBinding? = null
    private val TAG = javaClass.simpleName
    private lateinit var dataForQuery: DataForQuery
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = LoadSpeseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Recupero i dati da stampare per le spese (dal main activity)
        if (arguments != null) {
            dataForQuery = MeseUtils.createDataForQueryFromMeseAnno(arguments?.getString("toolbarTitle").toString())!!

            //Stampo la lista delle spese
            SpesaUtils.printSpese(binding, requireContext(), dataForQuery)
            //Inoltre setto il titolo della toolbar al nome del mese mostrato
            (activity as MainActivity).supportActionBar?.title = arguments?.getString("toolbarTitle")

            //var totaleSpesaMensile = SpesaUtils.calcolaTotaleSpese(binding, requireContext(), dataForQuery);
        }



        //Faccio uscire/scomparire un menu a tendina se premo sulla barra sopra la lista
        binding.listaSpeseHeaderLayout.setOnClickListener {
            val lsphLayout = binding.listaSpeseHeaderLayout
            val height = lsphLayout.layoutParams.height
            val width = lsphLayout.layoutParams.width
            var moreLessHeight = 300

            Log.i(TAG, "${lsphLayout.tooltipText}")

            //Di default è closed --> apro/chiudo
            if(lsphLayout.tooltipText.contentEquals("closed")) {
                lsphLayout.tooltipText = "open"
                binding.listaSpeseHeaderOpenCloseMenu.imageResource = R.drawable.ic_arrow_up
            }else{
                moreLessHeight *= -1
                lsphLayout.tooltipText = "closed"
                binding.listaSpeseHeaderOpenCloseMenu.imageResource = R.drawable.ic_arrow_down
            }

            val lp : RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(width,height+moreLessHeight)
            lsphLayout.layoutParams = lp
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}