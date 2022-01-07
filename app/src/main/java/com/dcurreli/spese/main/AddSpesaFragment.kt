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
import com.google.firebase.database.DatabaseReference
import java.util.*

class AddSpesaFragment : Fragment(R.layout.add_spesa) {

    private val TAG = javaClass.simpleName
    private var _binding: AddSpesaBinding? = null
    private lateinit var db: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = AddSpesaBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Calendario
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.spesaData.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this.requireContext(),
                DatePickerDialog.OnDateSetListener { _, mYear, mMonth, mDay ->
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

        //Se premo lo sfondo
        binding.addSpesaConstraintLayout.setOnClickListener {
            GenericUtils.hideSoftKeyBoard(requireContext(), view) //Chiudo la tastiera
            SpesaUtils.clearTextViewFocus(binding) //Tolgo il focus dagli altri bottoni
        }

        //Bottone "Aggiungi"
        binding.spesaButtonAddSpesa.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            if (binding.spesaSpesaText.text.isNullOrBlank()) {
                GenericUtils.showSnackbarError("Campo spesa non popolato !", binding)
            } else if (binding.spesaImporto.text.isNullOrBlank()) {
                GenericUtils.showSnackbarError("Campo importo non popolato !", binding)
            } else if (binding.spesaData.text.isNullOrBlank()) {
                GenericUtils.showSnackbarError("Campo data non popolato !", binding)
            } else if (binding.spesaPagatoreText.text.isNullOrBlank()) {
                GenericUtils.showSnackbarError("Campo pagatore non popolato !", binding)
            } else {
                //Recupero dati dall'xml
                SpesaUtils.creaSepsa(binding)
                GenericUtils.showSnackbarOK("Spesa creata : )", binding)

                findNavController().navigate(R.id.action_AddSpesaFragment_to_HomeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}