package com.dcapps.spese.view.loadspese

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcapps.spese.R
import com.dcapps.spese.adapters.ViewPagerAdapter
import com.dcapps.spese.data.viewmodel.ExpensesListViewModel
import com.dcapps.spese.databinding.LoadSpeseBinding
import com.dcapps.spese.enums.bundle.BundleArgumentsEnum
import com.dcapps.spese.enums.firebase.deeplink.DeepLinkParametersEnum
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.view.MainActivity
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


class LoadSpeseFragment : Fragment(R.layout.load_spese) {

    private var _binding: LoadSpeseBinding? = null
    private val className = javaClass.simpleName
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

        //Configurazione bottone aggiunta spesa
        setupAddSpesaButton()

        //Setup tab layout (schede orizzontali)
        setupTabLayout()

        //Nascondo bottom nav
        (activity as MainActivity).setBottomNavVisibility(false)

        return this.binding.root
    }

    override fun onDestroyView() {
        //Quando esco dal fragment rimuovo il sottotitolo
        GenericUtils.clearSottotitoloToolbar ((activity as AppCompatActivity?))

        super.onDestroyView()
        _binding = null
    }

    private fun setupAddSpesaButton() {
        listaSpeseModel.findByID(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString())
        listaSpeseModel.expensesListLiveData.observe(viewLifecycleOwner) { expensesList ->
            binding.addSpesaButton.visibility = if(expensesList?.paid == true) View.GONE else View.VISIBLE
        }

        binding.addSpesaButton.setOnClickListener{
            findNavController().navigate(R.id.action_loadSpeseFragment_to_addSpesaFragment, arguments)
        }
    }

    private fun setupTabLayout(){
        //Setup tab layout (schede orizzontali)
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(TabSpeseListaSpeseFragment.newInstance(arguments), "Spese", R.drawable.ic_shopping_cart)
        adapter.addFragment(TabSaldoListaSpeseFragment.newInstance(arguments), "Saldo", R.drawable.ic_euro)
        binding.viewPagerSchede.adapter = adapter
        binding.tableLayoutSchede.setupWithViewPager(binding.viewPagerSchede)

        //Aggiungo le icone ad ogni scheda -- ciclo quindi il codice non cambierà mai
        for(i in 0 until adapter.count){
            binding.tableLayoutSchede.getTabAt(i)!!.setIcon(adapter.getIcon(i))
        }
    }

    private fun setupToolbar() {
        //Faccio apparire il tasto share
        setHasOptionsMenu(true)

        //Cambio il titolo della toolbar
        (activity as MainActivity).setToolbarTitle(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_NAME.value))
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemShare : MenuItem = menu.findItem(R.id.share)
        val itemEdit : MenuItem = menu.findItem(R.id.edit)
        itemShare.isVisible = true
        itemEdit.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val listID = arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value)
        when (item.itemId) {
            R.id.share -> {
                //Gestione dello share
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, "Ciao, entra nel gruppo ${generateDynamicLink(listID).uri}")
                intent.type = "text/plain"

                startActivity(Intent.createChooser(intent, "Condividi la lista con: "))
            }
            R.id.edit -> {
                //Navigo sul fragment successivo passandogli il bundle con i dati ricevuti in precedenza
                findNavController().navigate(R.id.action_loadSpeseFragment_to_listaSettingsFragment, arguments)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun generateDynamicLink(listID: String?): DynamicLink {
        return FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setDomainUriPrefix("https://spesedc.page.link/join")
            .setLink(Uri.parse("https://spesedc.page.link/join"))
            .setLongLink(Uri.parse("https://spesedc.page.link/?link=https://spesedc.page.link/join?${DeepLinkParametersEnum.LIST.value}=$listID&apn=com.dcurreli.spese"))
            .buildDynamicLink()
    }

}