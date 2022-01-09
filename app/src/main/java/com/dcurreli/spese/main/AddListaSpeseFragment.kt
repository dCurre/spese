package com.dcurreli.spese.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.AddListaSpeseBinding
import com.dcurreli.spese.enum.CategoriaListaEnumUtils
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.ListaSpeseUtils
import com.google.firebase.database.DatabaseReference

class AddListaSpeseFragment : Fragment(R.layout.add_lista_spese) {

    private val TAG = javaClass.simpleName
    private var _binding: AddListaSpeseBinding? = null
    private lateinit var db: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddListaSpeseBinding.inflate(inflater, container, false)

        //Setup dropdown categoria
        setupCategoriaDropdown()

        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Se premo lo sfondo
        binding.addListaSpeseConstraintLayout.setOnClickListener {
            GenericUtils.hideSoftKeyBoard(requireContext(), view) //Chiudo la tastiera
            ListaSpeseUtils.clearTextViewFocus(binding) //Tolgo il focus dagli altri bottoni
        }

        //Setup bottone "Aggiungi"
        setupAddButton(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCategoriaDropdown(){
        val categoriaListAdapter = ArrayAdapter(requireContext(),R.layout.add_lista_spese_categoria_item, CategoriaListaEnumUtils.getEnums())
        binding.listaSpeseCategorieMenu.setAdapter(categoriaListAdapter)
    }

    private fun setupAddButton(view: View) {
        binding.spesaButtonAddSpesa.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            if (binding.listaSpeseNomeText.text.isNullOrBlank()) {
                GenericUtils.showSnackbarError("Nome lista non inserito !", binding.addListaSpeseConstraintLayout)
            } else if(binding.listaSpeseCategorieMenu.text.isNullOrBlank()){
                GenericUtils.showSnackbarError("Nome categoria non inserito !", binding.addListaSpeseConstraintLayout)
            }else {
                //Recupero dati dall'xml
                ListaSpeseUtils.creaListaSpese(binding)
                GenericUtils.showSnackbarOK("Lista creata : )", binding.addListaSpeseConstraintLayout)

                findNavController().navigate(R.id.action_addListaSpeseFragment_to_homeFragment)

            }
        }
    }
}