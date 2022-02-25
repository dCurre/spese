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
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.MeseUtils
import com.dcurreli.spese.utils.SpesaUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class AddSpesaFragment : Fragment(R.layout.add_spesa) {

    private val className = javaClass.simpleName
    private var _binding: AddSpesaBinding? = null
    private lateinit var idLista : String
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.SPESA.value)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = AddSpesaBinding.inflate(inflater, container, false)

        //Setto il nome della toolbar in base al bottone di spesa che ho clickato
        setupToolbar()

        //Setup calendario
        setupCalendario()

        //Setup autocomplete fields
        setupAutocompleteInputs()

        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                binding.spesaSpesaText.text.isNullOrBlank() -> { GenericUtils.showSnackbarError("Campo spesa non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaImporto.text.isNullOrBlank() -> { GenericUtils.showSnackbarError("Campo importo non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaData.text.isNullOrBlank() -> { GenericUtils.showSnackbarError("Campo data non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaPagatoreText.text.isNullOrBlank() -> { GenericUtils.showSnackbarError("Campo pagatore non popolato !", binding.addSpesaConstraintLayout) }
                else -> {
                    SpesaUtils.creaSepsa(binding, arguments?.getString("idLista").toString())
                    GenericUtils.showSnackbarOK("Spesa creata : )", binding.addSpesaConstraintLayout)

                    findNavController().navigate(R.id.loadSpeseFragment, arguments)
                }
            }
        }

        //Bottone "Aggiungi"
        binding.spesaButtonAddSpesa10.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            when {
                binding.spesaSpesaText.text.isNullOrBlank() -> { GenericUtils.showSnackbarError("Campo spesa non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaImporto.text.isNullOrBlank() -> { GenericUtils.showSnackbarError("Campo importo non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaData.text.isNullOrBlank() -> { GenericUtils.showSnackbarError("Campo data non popolato !", binding.addSpesaConstraintLayout) }
                binding.spesaPagatoreText.text.isNullOrBlank() -> { GenericUtils.showSnackbarError("Campo pagatore non popolato !", binding.addSpesaConstraintLayout) }
                else -> {

                    for(i in 0 until 10){
                        SpesaUtils.creaSepsa(binding, arguments?.getString("idLista").toString())
                    }
                    GenericUtils.showSnackbarOK("Spesa creata : )", binding.addSpesaConstraintLayout)

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
        val spesaText : AutoCompleteTextView = binding.spesaSpesaText
        val pagatoreText : AutoCompleteTextView = binding.spesaPagatoreText
        val arraySpesa : ArrayList<String> = ArrayList()
        val arrayPagatore : ArrayList<String> = ArrayList()
        val arraySpesaTemp : ArrayList<String> = ArrayList()
        val arrayPagatoreTemp : ArrayList<String> = ArrayList()

        db.orderByChild("listaSpesaID").equalTo(arguments?.getString("idLista").toString()).addValueEventListener(object :
            ValueEventListener {
            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //TODO cercare un modo più intelligente senza usare le liste temporanee
                arraySpesa.clear()
                arrayPagatore.clear()

                //Ciclo per ottenere spese e pagatori
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                    arraySpesaTemp.add(spesa.spesa)
                    arrayPagatoreTemp.add(spesa.pagatore)
                }

                if(arraySpesaTemp.isNotEmpty()){
                    arraySpesa.addAll(arraySpesaTemp.distinct())
                }

                if(arrayPagatoreTemp.isNotEmpty()) {
                    arrayPagatore.addAll(arrayPagatoreTemp.distinct())
                }

                spesaText.setAdapter(ArrayAdapter(requireContext(), R.layout.add_spesa_custom_spinner, arraySpesa))
                pagatoreText.setAdapter(ArrayAdapter(requireContext(), R.layout.add_spesa_custom_spinner, arrayPagatore))
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
        binding.spesaData.setText(MeseUtils.formatData(Calendar.getInstance().time))

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