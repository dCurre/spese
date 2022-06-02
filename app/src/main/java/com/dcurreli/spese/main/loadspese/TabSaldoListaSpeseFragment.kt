package com.dcurreli.spese.main.loadspese

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.SaldoCategoryAdapter
import com.dcurreli.spese.data.viewmodel.ListaSpeseViewModel
import com.dcurreli.spese.data.viewmodel.SpesaViewModel
import com.dcurreli.spese.databinding.LoadSpeseTabSaldoBinding
import com.dcurreli.spese.objects.SaldoCategory
import com.dcurreli.spese.objects.SaldoSubItem
import com.dcurreli.spese.utils.GenericUtils
import android.view.View as View1

class TabSaldoListaSpeseFragment : Fragment(R.layout.load_spese_tab_saldo) {

    private var _binding: LoadSpeseTabSaldoBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!
    private lateinit var saldoCategoryAdapter : SaldoCategoryAdapter
    private lateinit var spesaModel : SpesaViewModel
    private lateinit var listaSpeseViewModel: ListaSpeseViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = LoadSpeseTabSaldoBinding.inflate(inflater, container, false)
        saldoCategoryAdapter = SaldoCategoryAdapter(requireContext())
        spesaModel = ViewModelProvider(this)[SpesaViewModel::class.java]
        listaSpeseViewModel = ViewModelProvider(this)[ListaSpeseViewModel::class.java]
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        printSaldo()

        //Stampo le spese

        /*SpesaUtils.printSaldo(
            binding,
            requireContext(),
            idListaSpese = arguments?.getString("idLista").toString(), //id lista
            viewLifecycleOwner,
            ViewModelProvider(this)[ListaSpeseViewModel::class.java]
        )*/
    }

    private fun printSaldo() {
        //Aggiungo le spese estratte all'adapter
        spesaModel.findByListaSpesaID(arguments?.getString("idLista").toString())
        spesaModel.spesaListLiveData.observe(viewLifecycleOwner) { spesaList ->
            val mapSaldo = mutableMapOf<String, Double>()
            val dareAvereSUBITEMSArray = ArrayList<SaldoSubItem>()
            val dareAvereArray = ArrayList<SaldoCategory>()

            if (spesaList.isEmpty()) {
                binding.saldoNotPrintable.visibility = android.view.View.VISIBLE //MOSTRO LO SFONDO D'ERRORE
                binding.totaleListaSpese.visibility =  android.view.View.INVISIBLE //NASCONDO SCRITTA TOTALE
            } else {
                binding.saldoNotPrintable.visibility = android.view.View.INVISIBLE //NASCONDO LO SFONDO D'ERRORE
                binding.totaleListaSpese.visibility =  android.view.View.VISIBLE //MOSTRO SCRITTA TOTALE
            }

            //Riempio una mappa di <Pagatore, ImportiPagati>
            for (spesa in spesaList) {
                //Se la mappa non contiene il pagatore lo aggiungo, altrimenti sommo all'importo che già aveva
                if (mapSaldo.containsKey(spesa.pagatore)) {
                    mapSaldo[spesa.pagatore] = spesa.importo + mapSaldo[spesa.pagatore]!!
                } else {
                    mapSaldo[spesa.pagatore] = spesa.importo
                }
            }

            //AGGIORNO IL TOTALE A SCHERMO
            listaSpeseViewModel.findById(arguments?.getString("idLista").toString())
            listaSpeseViewModel.listaSpeseLiveData.observe(viewLifecycleOwner) { listaSpese ->
                binding.totaleListaSpese.setTextColor(if (listaSpese.isSaldato) ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark) else ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                binding.totaleListaSpese.text = GenericUtils.importoAsEur(mapSaldo.values.sum())
            }

            /* Riempio la lista di subitem e calcolo l'importo corretto dovuto
                FORMULA -> (A - B) / C

                A -> Spesa ricevente
                B -> Spesa pagatore
                C -> Size mappa, ovvero tutti gli utenti che hanno partecipato alle spese*/
            mapSaldo.forEach { pagatore ->
                mapSaldo.filterKeys { it != pagatore.key }.forEach { ricevente ->
                    dareAvereSUBITEMSArray.add(
                        SaldoSubItem(
                            pagatore.key,
                            ricevente.key,
                            (ricevente.value - pagatore.value) / mapSaldo.size
                        )
                    )
                }
            }

            /* Scorro nuovamente la mappa, per ogni pagatore aggiungo all'array di DareAvere:
                1. Pagatore
                2. Somma pagata dal pagatore
                3. Lista di debiti con altri utenti (filtrata dalla mappa per chiave pagatore) */
            mapSaldo.toSortedMap().forEach { entry ->
                dareAvereArray.add(
                    SaldoCategory(
                        entry.key,
                        entry.value,
                        dareAvereSUBITEMSArray.filter {
                            it.pagatore.equals(
                                entry.key,
                                ignoreCase = true
                            )
                        } as ArrayList<SaldoSubItem>?
                    )
                )
            }

            saldoCategoryAdapter.addItems(dareAvereArray)
        }

        binding.listaSaldoCategory.layoutManager = LinearLayoutManager(context)
        binding.listaSaldoCategory.adapter = saldoCategoryAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(args: Bundle?): TabSaldoListaSpeseFragment{
            val fragment = TabSaldoListaSpeseFragment().apply{
                arguments =  args
            }
            return fragment
        }
    }

}