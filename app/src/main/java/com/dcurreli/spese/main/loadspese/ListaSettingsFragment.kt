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
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.utils.GenericUtils
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import java.io.File
import java.io.FileOutputStream

class ListaSettingsFragment : Fragment(R.layout.lista_settings_fragment) {

    private val className = javaClass.simpleName
    private var _binding: ListaSettingsFragmentBinding? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    private var db: DatabaseReference = Firebase.database.reference.child(TablesEnum.SPESA.value)

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

        val nomeLista = "Riepilogo spese ${arguments?.getString("nomeLista").toString()}"
        val filePathName = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/${nomeLista}.xlsx"
        val filePath = File(filePathName);

        //Scrivo le spese nel file
        db.orderByChild("listaSpesaID").equalTo(arguments?.getString("idLista").toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hssfWorkbook = HSSFWorkbook()
                val hssfSheet: HSSFSheet = hssfWorkbook.createSheet(nomeLista)

                //Testata del file
                printHeaderRow(0, hssfSheet)

                var i = 1
                //Ciclo per ottenere spese e le stampo
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                    printRow(i, spesa.spesa, spesa.importo, spesa.data, spesa.pagatore, hssfSheet)
                    i++
                }

                //Stampo la somma degli importi
                printFormula(i+1, "Totale", "SUM(B1:B$i)", hssfSheet)

                //Creo il file
                try {
                    val fileOutputStream = FileOutputStream(filePath)
                    hssfWorkbook.write(fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()

                    GenericUtils.showSnackbarOK("Scaricato in $filePathName", binding.root)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e(className, "Failed to read value.", error.toException())
            }
        })
    }

    private fun printFormula(i: Int, descFormula: String, formula: String, hssfSheet: HSSFSheet) {
        val hssfRow: HSSFRow = hssfSheet.createRow(i)
        createCell(0, descFormula, hssfRow)
        createCellFormula(1, formula, hssfRow)

    }

    private fun printRow(i: Int, spesa : String, importo : Double, data : String, pagatore : String, hssfSheet: HSSFSheet) {
        val hssfRow: HSSFRow = hssfSheet.createRow(i)
        createCell(0, spesa, hssfRow)
        createCellAsNumber(1, importo, hssfRow)
        createCell(2, data, hssfRow)
        createCell(3, pagatore, hssfRow)
    }

    private fun printHeaderRow(i: Int, hssfSheet: HSSFSheet) {
        val hssfRow: HSSFRow = hssfSheet.createRow(i)
        createCell(0, "Spesa", hssfRow)
        createCell(1, "Importo", hssfRow)
        createCell(2, "Data", hssfRow)
        createCell(3, "Pagatore", hssfRow)
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

    private fun createCell(column: Int, text: String, hssfRow: HSSFRow) {
        val hssfCell: HSSFCell = hssfRow.createCell(column)
        hssfCell.setCellValue(text)

    }

    private fun createCellAsNumber(column: Int, number: Double, hssfRow: HSSFRow) {
        val hssfCell: HSSFCell = hssfRow.createCell(column)
        hssfCell.setCellValue(number)

    }

    private fun createCellFormula(column: Int, text: String, hssfRow: HSSFRow) {
        val hssfCell: HSSFCell = hssfRow.createCell(column)
        hssfCell.setCellType(CellType.FORMULA)
        hssfCell.cellFormula = text
    }
}