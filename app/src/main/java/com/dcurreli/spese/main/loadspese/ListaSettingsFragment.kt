package com.dcurreli.spese.main.loadspese

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dcurreli.spese.R
import com.dcurreli.spese.databinding.ListaSettingsFragmentBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.objects.ListaSpese
import com.dcurreli.spese.utils.GenericUtils
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class ListaSettingsFragment : Fragment(R.layout.lista_settings_fragment) {

    private val className = javaClass.simpleName
    private var _binding: ListaSettingsFragmentBinding? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.LISTE.value)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ListaSettingsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwitchTheme()

        binding.buttonAbbandona.setOnClickListener {
            leaveLista()
        }

        binding.buttonEsportaLista.setOnClickListener {
            esportaLista()
        }

        binding.switchSaldato.setOnCheckedChangeListener { _, checkedId ->
            GenericUtils.onOffSaldato(db, arguments?.getString("idLista").toString(), checkedId)
        }

        setupToolbar()
    }

    private fun esportaLista() {
        //Se l'utente non ha i permessi non posso scaricare il file
        (activity as MainActivity).checkUserPermission()

        val nomeLista = arguments?.getString("nomeLista").toString()
        val idLista = arguments?.getString("idLista").toString()
        val filePath = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${nomeLista}_${idLista}.xlsx");

        val hssfWorkbook = HSSFWorkbook()
        val hssfSheet: HSSFSheet = hssfWorkbook.createSheet(nomeLista)

        val hssfRow: HSSFRow = hssfSheet.createRow(0)
        val hssfCell: HSSFCell = hssfRow.createCell(0)

        hssfCell.setCellValue("editTextExcel.getText().toString()")

        try {
            if (!filePath.exists()) {
                filePath.createNewFile()
            }
            val fileOutputStream = FileOutputStream(filePath)
            hssfWorkbook.write(fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            GenericUtils.showSnackbarOK("Scaricato", binding.root)
        } catch (e: Exception) {
            e.printStackTrace()
            GenericUtils.showSnackbarError("Non scaricato", binding.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun leaveLista(){
    }

    private fun setupSwitchTheme(){
        if (arguments != null) {
            db.child(arguments?.getString("idLista").toString()).get().addOnSuccessListener {
                //Se non esiste creo l'utente nella lista utenti
                if (it.exists()) {
                    val lista : ListaSpese = it.getValue(ListaSpese::class.java) as ListaSpese
                    when (lista.isSaldato) {
                        true -> binding.switchSaldato.isChecked = true
                        false -> binding.switchSaldato.isChecked = false
                    }
                }
            }.addOnFailureListener {
                Log.e(className, "<<Error getting utente", it)
            }
        }
    }

    private fun setupToolbar() {
        //Cambio il titolo della toolbar
        (activity as MainActivity).setToolbarTitle("Impostazioni ${arguments?.getString("nomeLista")}")
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }
}