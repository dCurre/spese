package com.dcurreli.spese

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.SpesaUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.database.reference

        //Calendario
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        //Genero e formatto today
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val todayDate: String = formatter.format(Calendar.getInstance().time) //formatta today

        //Setto today dentro text view 'data'
        binding.textViewData.text = todayDate

        binding.buttonShowCalendar.setOnClickListener{
            val datePickerDialog = DatePickerDialog(this.requireContext(),DatePickerDialog.OnDateSetListener{ _, mYear, mMonth, mDay ->
                //Setto nella text view
                binding.textViewData.text = "${String.format("%02d",mDay)}/${String.format("%02d",(mMonth+1))}/$mYear"
            }, year, month, day)
            datePickerDialog.show()
        }

        //binding.buttonAddSpesa.isEnabled = false

        SpesaUtils.areSpesaFieldValid(binding)


        binding.buttonAddSpesa.setOnClickListener {
            //Chiudo la tastiera come prima cosa
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            if(binding.editTextSpesa.text.isNullOrBlank()){
                GenericUtils.showSnackbarError("Campo spesa non popolato !", binding)
            }else if(binding.editTextImporto.text.isNullOrBlank()){
                GenericUtils.showSnackbarError("Campo importo non popolato !", binding)
            }else if(binding.editTextPagatore.text.isNullOrBlank()){
                GenericUtils.showSnackbarError("Campo pagatore non popolato !", binding)
            }else{
                //Recupero dati dall'xml
                SpesaUtils.creaSepsa(db, binding)
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