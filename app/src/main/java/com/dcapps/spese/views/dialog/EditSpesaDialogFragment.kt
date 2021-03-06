package com.dcapps.spese.views.dialog

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.dcapps.spese.R
import com.dcapps.spese.adapters.ExpenseAdapter
import com.dcapps.spese.data.entities.Expense
import com.dcapps.spese.data.viewmodels.ExpenseViewModel
import com.dcapps.spese.enums.entity.ExpenseFieldsEnum
import com.dcapps.spese.utils.GenericUtils
import com.dcapps.spese.utils.GenericUtils.dateStringToTimestampSeconds
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import kotlin.properties.Delegates

class EditSpesaDialogFragment : DialogFragment() {

    private lateinit var expenseViewModel : ExpenseViewModel
    private lateinit var expenseAdapter : ExpenseAdapter
    private var absoluteAdapterPosition by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_expense_dialog, container, false)

        expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]

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

                    val updateMap: HashMap<String, Any> = HashMap()
                    updateMap[ExpenseFieldsEnum.EXPENSE.value] = spesa.text.toString().trim()
                    updateMap[ExpenseFieldsEnum.AMOUNT.value] = importo.text.toString().trim().replace(",", ".").toDouble()
                    updateMap[ExpenseFieldsEnum.EXPENSE_DATE.value] = data.text.toString().trim()
                    updateMap[ExpenseFieldsEnum.EXPENSE_DATE_TIMESTAMP.value] = dateStringToTimestampSeconds(data.text.toString().trim())
                    updateMap[ExpenseFieldsEnum.BUYER.value] = pagatore.text.toString().trim()

                    //Update della spesa
                    expenseViewModel.update(arguments?.getString("id")!!, updateMap)

                    //Chiudo il dialog
                    dismiss()
                }
            }
        }

        //Bottone "X"
        exitButton.setOnClickListener { dismiss() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        //Resets the slider
        expenseAdapter.notifyItemChanged(absoluteAdapterPosition)
        super.onDismiss(dialog)
    }

    private fun clearErrors(view: View) {
        view.findViewById<TextInputLayout>(R.id.edit_spesa_spesa_layout).error = null
        view.findViewById<TextInputLayout>(R.id.edit_spesa_importo_layout).error = null
        view.findViewById<TextInputLayout>(R.id.edit_spesa_pagatore_layout).error = null
    }

    private fun setupInputText(view: View) {
        view.findViewById<TextInputEditText>(R.id.edit_spesa_text).setText(arguments?.getString("spesa"))
        view.findViewById<TextInputEditText>(R.id.edit_spesa_importo).setText(arguments?.getString("importo"))
        view.findViewById<TextInputEditText>(R.id.edit_spesa_pagatore_text).setText(arguments?.getString("pagatore"))
    }

    fun newInstance(expense: Expense, expenseAdapter: ExpenseAdapter, absoluteAdapterPosition: Int): EditSpesaDialogFragment {

        val bundle = Bundle()
        bundle.putString("id", expense.id)
        bundle.putString("spesa", expense.expense)
        bundle.putString("importo", expense.amount.toString())
        bundle.putString("data", expense.expenseDate)
        bundle.putString("pagatore", expense.buyer)
        val dialogFragment = EditSpesaDialogFragment()
        dialogFragment.arguments = bundle
        dialogFragment.expenseAdapter = expenseAdapter
        dialogFragment.absoluteAdapterPosition = absoluteAdapterPosition
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

                    GenericUtils.clearTextViewFocusEditSpesa(view) //Tolgo il focus dagli altri bottoni
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }
}
