package com.dcapps.spese.views.loadexpenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcapps.spese.R
import com.dcapps.spese.adapters.BalanceCategoryAdapter
import com.dcapps.spese.data.dto.balance.BalanceCategory
import com.dcapps.spese.data.dto.balance.BalanceSubItem
import com.dcapps.spese.data.viewmodels.ExpenseViewModel
import com.dcapps.spese.data.viewmodels.ExpensesListViewModel
import com.dcapps.spese.databinding.LoadSpeseTabSaldoBinding
import com.dcapps.spese.enums.bundle.BundleArgumentsEnum
import com.dcapps.spese.utils.GenericUtils
import android.view.View as View1

class ExpensesListBalanceFragment : Fragment(R.layout.load_spese_tab_saldo) {

    private var _binding: LoadSpeseTabSaldoBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!
    private lateinit var balanceCategoryAdapter : BalanceCategoryAdapter
    private lateinit var spesaModel : ExpenseViewModel
    private lateinit var expensesListViewModel: ExpensesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = LoadSpeseTabSaldoBinding.inflate(inflater, container, false)
        balanceCategoryAdapter = BalanceCategoryAdapter(requireContext())
        spesaModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        expensesListViewModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        printBalance()
    }

    private fun printBalance() {

        spesaModel.findAllByExpensesListID(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString())
        spesaModel.expenseListLiveData.observe(viewLifecycleOwner) { expenseList ->
            val mapSaldo = mutableMapOf<String, Double>()
            val balanceCategorySubItems = ArrayList<BalanceSubItem>()
            val balanceCategoryList = ArrayList<BalanceCategory>()

            if (expenseList.isEmpty()) {
                binding.saldoNotPrintable.visibility = android.view.View.VISIBLE //ERROR BACKGROUND
                binding.totaleListaSpese.visibility =  android.view.View.INVISIBLE //TOTAL STRING HIDDEN
            } else {
                binding.saldoNotPrintable.visibility = android.view.View.INVISIBLE //ERROR BACKGROUND HIDDEN
                binding.totaleListaSpese.visibility =  android.view.View.VISIBLE //TOTAL STRING
            }

            //Filling a map of <Buyer, paidAmount>
            for (expense in expenseList) {
                // Only new buyer gets added else the import gets summed to the previous one
                if (mapSaldo.containsKey(expense.buyer)) {
                    mapSaldo[expense.buyer] = expense.amount + mapSaldo[expense.buyer]!!
                } else {
                    mapSaldo[expense.buyer] = expense.amount
                }
            }

            //UPDATE TOTAL
            expensesListViewModel.findByID(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString())
            expensesListViewModel.expensesListLiveData.observe(viewLifecycleOwner) { expensesLists ->
                binding.totaleListaSpese.setTextColor(if (expensesLists?.paid == true) ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark) else ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                binding.totaleListaSpese.text = GenericUtils.importoAsEur(mapSaldo.values.sum())
            }

            /* Riempio la lista di subitem e calcolo l'importo corretto dovuto
                FORMULA -> (A - B) / C

                A -> Spesa ricevente
                B -> Spesa pagatore
                C -> Size mappa, ovvero tutti gli utenti che hanno partecipato alle spese*/
            mapSaldo.forEach { buyer ->
                mapSaldo.filterKeys { it != buyer.key }.forEach { receiver ->
                    balanceCategorySubItems.add(
                        BalanceSubItem(
                            buyer.key,
                            receiver.key,
                            (receiver.value - buyer.value) / mapSaldo.size
                        )
                    )
                }
            }

            /* Scorro nuovamente la mappa, per ogni pagatore aggiungo all'array di DareAvere:
                1. Pagatore
                2. Somma pagata dal pagatore
                3. Lista di debiti con altri utenti (filtrata dalla mappa per chiave pagatore) */
            mapSaldo.toSortedMap().forEach { entry ->
                balanceCategoryList.add(
                    BalanceCategory(
                        entry.key,
                        entry.value,
                        balanceCategorySubItems.filter { subItem ->
                            subItem.buyer.equals(
                                entry.key,
                                ignoreCase = true
                            )
                        } as ArrayList<BalanceSubItem>
                    )
                )
            }

            balanceCategoryAdapter.addItems(balanceCategoryList)
        }

        binding.listaSaldoCategory.layoutManager = LinearLayoutManager(context)
        binding.listaSaldoCategory.adapter = balanceCategoryAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(args: Bundle?): ExpensesListBalanceFragment{
            val fragment = ExpensesListBalanceFragment().apply{
                arguments =  args
            }
            return fragment
        }
    }

}