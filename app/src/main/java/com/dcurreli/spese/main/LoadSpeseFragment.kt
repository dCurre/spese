package com.dcurreli.spese.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.dcurreli.spese.objects.DataForQuery
import com.dcurreli.spese.utils.SpesaUtils
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk27.coroutines.onClick
import android.view.View as View1

class LoadSpeseFragment : Fragment(R.layout.load_spese) {

    private var _binding: LoadSpeseBinding? = null
    private val TAG = javaClass.simpleName
    private lateinit var dataForQuery: DataForQuery
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = LoadSpeseBinding.inflate(inflater, container, false)

        //Setto il nome della toolbar in base al bottone di spesa che ho clickato
        setupToolbarTitle()

        //Faccio uscire/scomparire un menu a tendina se premo sulla barra sopra la lista
        setupMenuATendinaTotale()

        //Configurazione bottone aggiunta spesa
        setupAddSpesaButton()

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Stampo le spese
        SpesaUtils.printSpese(
            binding,
            requireContext(),
            idListaSpese = arguments?.getString("idLista").toString() //id lista
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbarTitle() {
        if (arguments != null) { (activity as MainActivity).supportActionBar?.title = arguments?.getString("nomeLista") }
    }

    @SuppressLint("NewApi")
    private fun setupMenuATendinaTotale(){
        val lsphLayout = binding.listaSpeseHeaderLayout
        val height = lsphLayout.layoutParams.height
        val width = lsphLayout.layoutParams.width
        var moreLessHeight = 300

        //Faccio uscire/scomparire un menu a tendina se premo sulla barra sopra la lista
        binding.listaSpeseHeaderLayout.onClick {
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

    private fun setupAddSpesaButton() {
        binding.addSpesaButton.setOnClickListener{
            findNavController().navigate(R.id.addSpesaFragment, arguments)
        }
    }
}