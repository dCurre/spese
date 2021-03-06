package com.dcapps.spese.views.loadexpenses

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcapps.spese.R
import com.dcapps.spese.adapters.ViewPagerAdapter
import com.dcapps.spese.data.viewmodels.ExpensesListViewModel
import com.dcapps.spese.databinding.LoadSpeseBinding
import com.dcapps.spese.enums.bundle.BundleArgumentsEnum
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.views.MainActivity
import com.google.android.material.tabs.TabLayoutMediator


class LoadExpensesFragment : Fragment(R.layout.load_spese) {

    private var _binding: LoadSpeseBinding? = null
    private val binding get() = _binding!!
    private lateinit var listaSpeseModel : ExpensesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoadSpeseBinding.inflate(inflater, container, false)
        listaSpeseModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]

        //Setto il nome della toolbar in base al bottone di spesa che ho clickato
        setupToolbar()

        //Nascondo bottom nav
        (activity as MainActivity).setBottomNavVisibility(false)

        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setup tab layout (schede orizzontali)
        setupTabLayout()
    }

    override fun onDestroyView() {
        //Quando esco dal fragment rimuovo il sottotitolo
        GenericUtils.clearSottotitoloToolbar ((activity as AppCompatActivity?))

        super.onDestroyView()
        _binding = null
    }

    private fun setupTabLayout(){
        val adapter = ViewPagerAdapter(activity)
        adapter.addFragment(ExpensesPrinterFragment.newInstance(arguments), "Spese", ContextCompat.getDrawable(requireActivity(), R.drawable.ic_shopping_cart))
        adapter.addFragment(ExpensesBalanceFragment.newInstance(arguments), "Saldo", ContextCompat.getDrawable(requireActivity(), R.drawable.ic_euro))
        binding.viewPagerSchede.adapter = adapter

        TabLayoutMediator(binding.tableLayoutSchede, binding.viewPagerSchede) { tab, position ->
            tab.text = adapter.getTabTitle(position)
            tab.icon = adapter.getIcon(position)
        }.attach()
    }

    private fun setupToolbar() {
        //Faccio apparire il tasto share
        setHasOptionsMenu(true)

        //Cambio il titolo della toolbar
        (activity as MainActivity).setToolbarTitle(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_NAME.value))
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        //Showing settings button share button
        menu.findItem(R.id.list_settings).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> { }
            R.id.list_settings -> {
                //Navigo sul fragment successivo passandogli il bundle con i dati ricevuti in precedenza
                findNavController().navigate(R.id.action_loadSpeseFragment_to_listaSettingsFragment, arguments)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}