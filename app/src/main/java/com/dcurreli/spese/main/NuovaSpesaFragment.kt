package com.dcurreli.spese.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.data.viewmodel.ListaSpeseViewModel
import com.dcurreli.spese.data.viewmodel.UserViewModel
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.utils.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.*


class NuovaSpesaFragment : Fragment(R.layout.add_spesa) {

    private val className = javaClass.simpleName
    private var _binding: AddSpesaBinding? = null
    private var dbSpesa: DatabaseReference = DBUtils.getDatabaseReference(TablesEnum.SPESA)
    private lateinit var userModel : UserViewModel
    private lateinit var listaSpeseModel : ListaSpeseViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddSpesaBinding.inflate(inflater, container, false)
        userModel = ViewModelProvider(this)[UserViewModel::class.java]
        listaSpeseModel = ViewModelProvider(this)[ListaSpeseViewModel::class.java]
        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Setto il nome della toolbar in base al bottone di spesa che ho clickato
        setupToolbar()

        //Setup calendario
        setupCalendario()

        //Setup autocomplete fields
        setupAutocompleteInputs()

        //Se premo lo sfondo
        binding.addSpesaConstraintLayout.setOnClickListener {
            GenericUtils.hideSoftKeyBoard(requireContext(), view) //Chiudo la tastiera
            SpesaUtils.clearTextViewFocusAddSpesa(binding) //Tolgo il focus dagli altri bottoni
        }

        //Bottone "Aggiungi"
        binding.spesaButtonAddSpesa.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            when {
                binding.spesaSpesaText.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo spesa non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaImporto.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo importo non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaData.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo data non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaPagatoreText.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo pagatore non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaImporto.text.toString().toDouble().equals(0.00) -> { SnackbarUtils.showSnackbarError("Inserire importo maggiore di 0 !", binding.addSpesaConstraintLayout) }
                else -> {
                    SpesaUtils.creaSpesa(binding, arguments?.getString("idLista").toString())
                    SnackbarUtils.showSnackbarOK("Spesa creata : )", binding.addSpesaConstraintLayout)

                    findNavController().popBackStack()
                }
            }
        }

        //TODO: tasto debug da togliere
        if(!DBUtils.getLoggedUser()!!.email.equals("curre994@gmail.com", ignoreCase = true)){
            binding.spesaButtonAddSpesa10.visibility = View.GONE
        }
        //TODO: tasto debug da togliere
        binding.spesaButtonAddSpesa10.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            when {
                binding.spesaSpesaText.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo spesa non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaImporto.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo importo non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaData.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo data non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaPagatoreText.text.isNullOrBlank() -> { SnackbarUtils.showSnackbarError("Campo pagatore non popolato !", binding.addSpesaConstraintLayout) }
                else -> {
                    for(i in 0 until 10){
                        SpesaUtils.creaSpesa(binding, arguments?.getString("idLista").toString())
                    }
                    SnackbarUtils.showSnackbarOK("Spesa creata : )", binding.addSpesaConstraintLayout)

                    findNavController().navigate(R.id.loadSpeseFragment, arguments)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAutocompleteInputs() {
        val spesaText = binding.spesaSpesaText
        val pagatoreText = binding.spesaPagatoreText
        val arrayAdapterSpese = ArrayAdapter<String>(requireContext(), R.layout.add_spesa_custom_spinner)
        val arrayAdapterPagatori = ArrayAdapter<String>(requireContext(), R.layout.add_spesa_custom_spinner)
        val pagatoriList = ArrayList<String>()

        setupAutocompleteInputSpese(pagatoriList, arrayAdapterSpese, arrayAdapterPagatori)

        setupAutocompleteInputPagatori(pagatoriList, arrayAdapterPagatori)

        spesaText.setAdapter(arrayAdapterSpese)
        pagatoreText.setAdapter(arrayAdapterPagatori)

    }

    private fun setupAutocompleteInputPagatori(
        pagatoriList: ArrayList<String>,
        arrayAdapterPagatori: ArrayAdapter<String>
    ) {
        listaSpeseModel.getListaSpeseById(arguments?.getString("idLista").toString())
        listaSpeseModel.listaSpeseLiveData.observe(viewLifecycleOwner) { listaSpese ->
            val partecipanti = listaSpese.partecipanti
            var countPartecipanti = partecipanti.size

            userModel.getUserListByIdList(partecipanti)
            userModel.userListLiveData.observe(viewLifecycleOwner) { userList ->
                for(user in userList){
                    countPartecipanti--

                    //Recuperato l'utente lo aggiungo alla lista
                    pagatoriList.add(user.nominativo)

                    //Quando il contatore raggiunge lo 0, faccio la distinct per filtrarmi i doppioni e aggiungo alla lista
                    if(countPartecipanti == 0){
                        arrayAdapterPagatori.addAll(pagatoriList.distinct())
                    }
                }
            }
        }
    }

    private fun setupAutocompleteInputSpese(
        pagatoriList: ArrayList<String>,
        arrayAdapterSpese: ArrayAdapter<String>,
        arrayAdapterPagatori: ArrayAdapter<String>
    ) {
        dbSpesa.orderByChild("listaSpesaID").equalTo(arguments?.getString("idLista").toString()).addValueEventListener(object :
            ValueEventListener {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val speseList = ArrayList<String>()
                pagatoriList.clear()
                arrayAdapterSpese.clear()
                arrayAdapterPagatori.clear()

                //Ciclo per ottenere spese e pagatori
                for (snapshot in dataSnapshot.children) {
                    speseList.add(snapshot.child("spesa").getValue(String::class.java) as String)
                    pagatoriList.add(snapshot.child("pagatore").getValue(String::class.java) as String)
                }

                //Faccio la distinct per filtrarmi le spese doppie, i pagatori li aggiungo dopo
                arrayAdapterSpese.addAll(speseList.distinct())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(className, "Failed to read value.", error.toException())
            }
        })
    }

    private fun setupToolbar() {
        val titolo =  if(arguments == null) "Aggiungi una spesa" else "Aggiungi a ${arguments?.getString("nomeLista").toString()}"

        //Cambio il titolo della toolbar
        (activity as MainActivity).setToolbarTitle(titolo)
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }

    @SuppressLint("SetTextI18n")
    private fun setupCalendario() {
        //Calendario
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //Di base settato a today
        binding.spesaData.setText(DateUtils.formatData(Calendar.getInstance().time))

        binding.spesaData.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this.requireContext(),
                { _, mYear, mMonth, mDay ->
                    //Setto la data nella text view
                    binding.spesaData.setText(
                        "${
                            String.format(
                                "%02d",
                                mDay
                            )
                        }/${String.format("%02d", (mMonth + 1))}/$mYear"
                    )

                    SpesaUtils.clearTextViewFocusAddSpesa(binding) //Tolgo il focus dagli altri bottoni
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }
}