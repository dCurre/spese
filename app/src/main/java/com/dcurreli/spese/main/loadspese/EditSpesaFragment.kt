package com.dcurreli.spese.main.loadspese

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.EditSpesaDialogBinding
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.SpesaUtils
import android.view.View as View1

class EditSpesaFragment() : Fragment(R.layout.edit_spesa_dialog) {

    private var _binding: EditSpesaDialogBinding? = null
    private val TAG = javaClass.simpleName
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = EditSpesaDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Se premo lo sfondo
        binding.editSpesaConstraintLayout.setOnClickListener {
            GenericUtils.hideSoftKeyBoard(requireContext(), view) //Chiudo la tastiera
            SpesaUtils.clearTextViewFocusEditSpesa(binding) //Tolgo il focus dagli altri bottoni
            Log.i("SpesaAdapter", "provaaa")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}