package com.dcurreli.spese.main.dialog

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.dcurreli.spese.R
import com.dcurreli.spese.data.viewmodel.SpesaViewModel
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.SpesaUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class EditSpesaDialogFragment : DialogFragment() {

    private lateinit var spesaModel : SpesaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_spesa_dialog, container, false)

        spesaModel = ViewModelProvider(this)[SpesaViewModel::class.java]

        setupInputText(view)

        setupCalendario(view)

        setupButtons(view)

        return view
    }

    private fun setupButtons(view: View) {
        val spesa = view.findViewById<TextInputEditText>(R.id.edit_spesa_text)
        val spesaLayout = view.findViewById<TextInputLayout>(R.id.edit_spesa_spesa_layout)
        val importo = view.findViewById<TextInputEditText>(R.id.edit_spesa_importo)
        val importoLayout = view.findViewById<TextInputLayout>(R.id.edit_spesa_importo_layout)
        val pagatore = view.findViewById<TextInputEditText>(R.id.edit_spesa_pagatore_text)
        val pagatoreLayout = view.findViewById<TextInputLayout>(R.id.edit_spesa_pagatore_layout)
        val data = view.findViewById<TextInputEditText>(R.id.edit_spesa_data)
        val aggiornaButton = view.findViewById<MaterialButton>(R.id.edit_spesa_button_aggiorna_spesa)
        val exitButton = view.findViewById<MaterialButton>(R.id.edit_spesa_button_exit)

        //Bottone "Aggiorna"
        aggiornaButton.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)
            //Pulisco i messaggi d'errore
            clearErrors(view)

            //Stampo errori se ce ne sono
            when {
                spesa.text.isNullOrBlank() -> { spesaLayout.error = "Campo non popolato" }
                importo.text.isNullOrBlank() -> { importoLayout.error = "Campo non popolato" }
                pagatore.text.isNullOrBlank() -> { pagatoreLayout.error = "Campo non popolato" }
                importo.text.toString().toDouble().equals(0.00) -> { importoLayout.error = "Importo non valido" }
                else -> {

                    //Update della spesa
                    spesaModel.update(Spesa(
                        arguments?.getString("id")!!,
                        spesa.text.toString().trim(),
                        importo.text.toString().trim().replace(",", ".").toDouble(),
                        data.text.toString().trim(),
                        pagatore.text.toString().trim(),
                        GenericUtils.dateStringToTimestampSeconds(data.text.toString(), "dd/MM/yyyy").toString()
                    ))
                    dismiss()
                }
            }
        }

        //Bottone "X"
        exitButton.setOnClickListener { dismiss() }

    }

    private fun clearErrors(view: View) {
        view.findViewById<TextInputLayout>(R.id.edit_spesa_spesa_layout).error = null
        view.findViewById<TextInputLayout>(R.id.edit_spesa_importo_layout).error = null
        view.findViewById<TextInputLayout>(R.id.edit_spesa_pagatore_layout).error = null
    }

    private fun setupInputText(view: View) {
        val spesa = view.findViewById<TextInputEditText>(R.id.edit_spesa_text)
        val importo = view.findViewById<TextInputEditText>(R.id.edit_spesa_importo)
        val pagatore = view.findViewById<TextInputEditText>(R.id.edit_spesa_pagatore_text)

        spesa.setText(arguments?.getString("spesa"))
        importo.setText(arguments?.getString("importo"))
        pagatore.setText(arguments?.getString("pagatore"))
    }

    fun newInstance(spesa: Spesa): EditSpesaDialogFragment {
        val bundle = Bundle()
        bundle.putString("id", spesa.id)
        bundle.putString("spesa", spesa.spesa)
        bundle.putString("importo", spesa.importo.toString())
        bundle.putString("data", spesa.data)
        bundle.putString("pagatore", spesa.pagatore)
        val dialogFragment = EditSpesaDialogFragment()
        dialogFragment.arguments = bundle
        return dialogFragment
    }

    companion object {
        const val TAG = "EditSpesaDialogFragment"
    }

    @SuppressLint("SetTextI18n")
    private fun setupCalendario(view: View) {
        val data = view.findViewById<TextInputEditText>(R.id.edit_spesa_data)

        //Calendario
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //Di base settato a today
        data.setText(arguments?.getString("data"))

        data.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this.requireContext(),
                { _, mYear, mMonth, mDay ->
                    //Setto la data nella text view
                    data.setText(
                        "${
                            String.format(
                                "%02d",
                                mDay
                            )
                        }/${String.format("%02d", (mMonth + 1))}/$mYear"
                    )

                    SpesaUtils.clearTextViewFocusEditSpesa(view) //Tolgo il focus dagli altri bottoni
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }
}
