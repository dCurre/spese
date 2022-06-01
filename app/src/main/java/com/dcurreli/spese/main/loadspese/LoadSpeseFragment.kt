package com.dcurreli.spese.main.loadspese

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.ViewPagerAdapter
import com.dcurreli.spese.data.entity.ListaSpese
import com.dcurreli.spese.databinding.LoadSpeseBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.GenericUtils.createBundleForListaSpese
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


class LoadSpeseFragment : Fragment(R.layout.load_spese) {

    private var _binding: LoadSpeseBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!
    private var dbListaSpese = DBUtils.getDatabaseReference(TablesEnum.LISTE)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoadSpeseBinding.inflate(inflater, container, false)

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        //Quando esco dal fragment rimuovo il sottotitolo
        GenericUtils.clearSottotitoloToolbar ((activity as AppCompatActivity?))

        super.onDestroyView()
        _binding = null
    }

    private fun setupAddSpesaButton() {
        dbListaSpese.child(arguments?.getString("idLista").toString()).get().addOnSuccessListener {
            //Setto la visibilità del bottone 'add spesa'
            binding.addSpesaButton.visibility = if(it.exists() && (it.getValue(ListaSpese::class.java) as ListaSpese).isSaldato) View.GONE else View.VISIBLE

            binding.addSpesaButton.setOnClickListener{
                findNavController().navigate(R.id.action_loadSpeseFragment_to_addSpesaFragment, arguments)
            }
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
        (activity as MainActivity).setToolbarTitle(arguments?.getString("nomeLista"))
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemShare : MenuItem = menu.findItem(R.id.share)
        val itemEdit : MenuItem = menu.findItem(R.id.edit)
        itemShare.isVisible = true
        itemEdit.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val listID = arguments?.getString("idLista")
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
                //Navigo sul fragment successivo passandogli il bundle con id lista e nome lista
                //TODO togliere il metodo createBundleForListaSpese perchè è inutile
                findNavController().navigate(R.id.action_loadSpeseFragment_to_listaSettingsFragment, createBundleForListaSpese(arguments))
            }
        }

        return super.onOptionsItemSelected(item)
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