package com.dcurreli.spese

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dcurreli.spese.databinding.AddSpesaBinding
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.utils.SpesaUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import android.view.View as View1

class AddSpesaFragment : Fragment() {

    private var _binding: AddSpesaBinding? = null
    private val db = Firebase.firestore
    private val path = "spese"


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
                binding.textViewData.setText(""+mDay+"/"+(mMonth+1)+"/"+mYear)
            }, year, month, day)
            datePickerDialog.show()
        }

        binding.buttonAddSpesa.setOnClickListener {
            //Recupero dati dall'xml
            val luogo = binding.editTextSpesa.text.toString()
            val importo = binding.editTextImporto.text.toString().replace(",",".")
            val data = binding.textViewData.text.toString()
            val pagatore = binding.editTextPagatore.text.toString()

            val spesa = Spesa(
                luogo,
                importo.toDouble(),
                data,
                pagatore
            )
            db.collection(path).document(SpesaUtils.getSpesaPath(spesa)).set(spesa)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Spesa aggiunta! | Spesa: " + spesa.luogo + ", Importo: " + spesa.importo + ", Data: " + spesa.data + ", Pagatore: " + spesa.pagatore)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }
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