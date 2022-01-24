package com.dcurreli.spese.main.loadspese

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.ViewPagerAdapter
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.utils.GenericUtils
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import android.view.View as View1





class LoadSpeseFragment : Fragment(R.layout.load_spese) {

    private var _binding: LoadSpeseBinding? = null
    private val TAG = javaClass.simpleName
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {
        _binding = LoadSpeseBinding.inflate(inflater, container, false)

        //Setto il nome della toolbar in base al bottone di spesa che ho clickato
        setupToolbar()

        //Configurazione bottone aggiunta spesa
        setupAddSpesaButton()

        //Setup tab layout (schede orizzontali)
        setupTabLayout()

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()

        //Quando esco dal fragment rimuovo il sottotitolo
        GenericUtils.clearSottotitoloToolbar ((activity as AppCompatActivity?))

        _binding = null
    }

    private fun setupAddSpesaButton() {
        binding.addSpesaButton.setOnClickListener{
            findNavController().navigate(R.id.addSpesaFragment, arguments)
        }
    }

    private fun setupTabLayout(){
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(SpeseTabFragment.newInstance(arguments), "Spese", R.drawable.ic_settings)
        adapter.addFragment(SaldoTabFragment.newInstance(arguments), "Saldo", R.drawable.ic_calendar)
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

        //Cambio il titolo della lista
        val toolbar = (activity as MainActivity).supportActionBar
        if (arguments != null) { toolbar?.title = arguments?.getString("nomeLista") }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item : MenuItem = menu.findItem(R.id.share)

        //Gestisco gli eventi on click della toolbar su questo fragment
        item.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.share ->{
                    //Gestione dello share
                    val listID = arguments?.getString("idLista")

                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, "We we, entra nel gruppo ${generateDynamicLink(listID).uri}")
                    intent.type = "text/plain"

                    startActivity(Intent.createChooser(intent, "Condividi la lista con: "))
                }
            }
            true
        }

        //Mostro l'item
        item.isVisible = true
    }

    private fun generateDynamicLink(listID: String?): DynamicLink {
        return FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setDomainUriPrefix("https://spesedc.page.link/join")
            .setLink(Uri.parse("https://spesedc.page.link/join"))
            .setLongLink(Uri.parse("https://spesedc.page.link/?link=https://spesedc.page.link/join?group=$listID&apn=com.dcurreli.spese"))
            .buildDynamicLink()
    }
}