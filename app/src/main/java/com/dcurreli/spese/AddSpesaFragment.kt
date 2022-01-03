package com.dcurreli.spese

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.SpesaUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.support.v4.toast
import java.text.SimpleDateFormat
import java.util.*
import android.view.View as View1


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
    ): View1? {

        _binding = AddSpesaBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
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
            val datePickerDialog = DatePickerDialog(this.requireContext(),DatePickerDialog.OnDateSetListener{ view, mYear, mMonth, mDay ->
                //Setto nella text view
                binding.textViewData.text = "${String.format("%02d",mDay)}/${String.format("%02d",(mMonth+1))}/$mYear"
            }, year, month, day)
            datePickerDialog.show()
        }

        binding.buttonAddSpesa.setOnClickListener {
            //Recupero dati dall'xml
            val luogo = binding.editTextSpesa.text.toString()
            val importo = binding.editTextImporto.text.toString().replace(",",".")
            val data = binding.textViewData.text.toString()
            val pagatore = binding.editTextPagatore.text.toString()
            SpesaUtils.creaSepsa(db, luogo, importo, data, pagatore)
            toast("Spesa creata : )")

            //Chiudo la tastiera
            GenericUtils.hideSoftKeyBoard(requireContext(), view)

            findNavController().navigate(R.id.action_AddSpesaFragment_to_HomeFragment)
        }

        binding.backHomeButton.setOnClickListener {
            findNavController().navigate(R.id.action_AddSpesaFragment_to_HomeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}