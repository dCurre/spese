package com.dcurreli.spese.main

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.SpesaUtils
import java.util.*

class AddSpesaFragment : Fragment(R.layout.add_spesa) {

    private val TAG = javaClass.simpleName
    private var _binding: AddSpesaBinding? = null
    private lateinit var idLista : String

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
        setupToolbarTitle()

        //Setup calendario
        setupCalendario()

        return binding.root
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Se premo lo sfondo
        binding.addSpesaConstraintLayout.setOnClickListener {
            GenericUtils.hideSoftKeyBoard(requireContext(), view) //Chiudo la tastiera
            SpesaUtils.clearTextViewFocus(binding) //Tolgo il focus dagli altri bottoni
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbarTitle() {
        var nomeLista = "Aggiungi una spesa"

        if (arguments != null) {
            nomeLista = arguments?.getString("nomeLista").toString()
        }

        (activity as MainActivity).supportActionBar?.title = "Aggiungi a $nomeLista"
    }

    @SuppressLint("SetTextI18n")
    private fun setupCalendario() {
        //Calendario
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

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

                    SpesaUtils.clearTextViewFocus(binding) //Tolgo il focus dagli altri bottoni
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }
}