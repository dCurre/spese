package com.dcurreli.spese.main.loadspese

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.PartecipantiAdapter
import com.dcurreli.spese.databinding.ListaSettingsFragmentBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.MainActivity
import com.dcurreli.spese.objects.ListaSpese
import com.dcurreli.spese.objects.Spesa
import com.dcurreli.spese.objects.Utente
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.ExcelUtils
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.SnackbarUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class SettingsListaSpeseFragment : Fragment(R.layout.lista_settings_fragment) {

    private val className = javaClass.simpleName
    private var _binding: ListaSettingsFragmentBinding? = null
    private var dbSpesa: DatabaseReference = Firebase.database.reference.child(TablesEnum.SPESA.value)
    private var dbListaSpese: DatabaseReference = Firebase.database.reference.child(TablesEnum.LISTE.value)
    private var dbUtente: DatabaseReference = Firebase.database.reference.child(TablesEnum.UTENTE.value)

    // This property is only valid between onCreateView and onDestroyView.
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

        val idLista = arguments?.getString("idLista").toString()
        val nomeLista = arguments?.getString("nomeLista").toString()

        printPartecipanti(idLista)

        setupSwitches(idLista)

        setupButtons(idLista, nomeLista)

        setupToolbar(nomeLista)
    }

    private fun printPartecipanti(idLista: String) {
       dbListaSpese.child(idLista).get().addOnSuccessListener {
            if (it.exists()) {
                val listaSpese = it.getValue(ListaSpese::class.java) as ListaSpese
                val partecipantiArray = ArrayList<Utente>()
                val partecipantiAdapter = PartecipantiAdapter(partecipantiArray, listaSpese.owner)
                binding.listaPartecipanti.layoutManager = LinearLayoutManager(context)
                binding.listaPartecipanti.adapter = partecipantiAdapter

                dbUtente.addValueEventListener(object : ValueEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        partecipantiArray.clear()

                        //Ciclo per ottenere spese e totale
                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                            val utente = snapshot.getValue(Utente::class.java) as Utente

                            if(listaSpese.partecipanti.contains(utente.user_id)){
                                partecipantiArray.add(utente)
                            }
                        }

                        partecipantiAdapter.notifyDataSetChanged()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e(className, "Failed to read value.", error.toException())
                    }
                })
            }
       }
    }

    private fun setupButtons(idLista: String, nomeLista:  String) {

        showHideDeleteButton(idLista)

        binding.buttonDelete.setOnClickListener {
            cancellaLista(idLista)
        }

        binding.buttonAbbandona.setOnClickListener {
            abbandonaLista(idLista)
        }

        binding.buttonEsportaLista.setOnClickListener {
            esportaLista(idLista, nomeLista)
        }
    }

    private fun showHideDeleteButton(idLista: String) {
        dbListaSpese.child(idLista).get().addOnSuccessListener {
            if (it.exists()) {
                when {
                    (it.getValue(ListaSpese::class.java) as ListaSpese).owner.equals(DBUtils.getCurrentUser()!!.uid)-> { binding.buttonDelete.visibility = View.VISIBLE }
                    else -> { binding.buttonDelete.visibility = View.GONE }
                }
            }
        }
    }

    private fun cancellaLista(idLista: String) {

        AlertDialog.Builder(context)
            .setTitle("Conferma")
            .setMessage("Vuoi veramente cancellare la lista?")
            .setPositiveButton("SI") { _, _ ->
                //ELIMINO LE SPESE LEGATE A QUELLA LISTA
                dbSpesa.orderByChild("listaSpesaID").equalTo(idLista).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                            val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                            dbSpesa.child(spesa.idAsString()).removeValue()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e(className, "Failed to read value.", error.toException())
                    }
                })

                //ELIMINO LA LISTA
                dbListaSpese.child(idLista).removeValue()

                //REDIRECT MAIN PAGE
                activity?.finish()
                startActivity(Intent(Intent(context, MainActivity::class.java)))
            }
            .setNegativeButton("NO") { _, _ -> }
            .show()
    }

    private fun esportaLista(idLista: String, nomeLista: String) {
        //Se l'utente non ha i permessi non posso scaricare il file
        (activity as MainActivity).checkUserPermission()

        val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
        val completeFileName = "Riepilogo_spese_$nomeLista.xlsx"
        val completePath = "$downloadFolder/$completeFileName"
        val filePath = File(completePath)

        //Scrivo le spese nel file
        dbSpesa.orderByChild("listaSpesaID").equalTo(idLista).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hssfWorkbook = HSSFWorkbook()
                val hssfSheet: HSSFSheet = hssfWorkbook.createSheet(nomeLista)

                //Testata del file
                ExcelUtils.printHeaderRow(0, hssfSheet)

                var i = 1
                //Ciclo per ottenere spese e le stampo
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val spesa = snapshot.getValue(Spesa::class.java) as Spesa
                    ExcelUtils.printRow(i, spesa.spesa, spesa.importo, spesa.data, spesa.pagatore, hssfSheet)
                    i++
                }

                //Stampo la somma degli importi
                ExcelUtils.printFormula(i+1, "Totale", "SUM(B1:B$i)", hssfSheet)

                //Creo il file
                try {
                    val fileOutputStream = FileOutputStream(filePath)
                    hssfWorkbook.write(fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()

                    SnackbarUtils.showSnackbarOpenPath(
                        "\"$completeFileName\" scaricato in $downloadFolder",
                        binding.root,
                        downloadFolder,
                        requireContext()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(className, "Failed to read value.", error.toException())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun abbandonaLista(idLista: String){
        dbListaSpese.child(idLista).get().addOnSuccessListener {
            if (it.exists()) {
                val lista : ListaSpese = it.getValue(ListaSpese::class.java) as ListaSpese

                //Se la lista ha un solo partecipante
                if(lista.partecipanti.size == 1 && lista.partecipanti[0].equals(DBUtils.getCurrentUser()!!.uid)){
                    AlertDialog.Builder(context)
                        .setTitle("Conferma")
                        .setMessage("Sei l'unico partecipante di questa lista, vuoi cancellarla?")
                        .setPositiveButton("SI") { _, _ ->
                            cancellaLista(idLista)
                        }
                        .setNegativeButton("NO") { _, _ -> }
                        .show()
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Conferma")
                        .setMessage("Vuoi veramente abbandonare la lista?")
                        .setPositiveButton("SI") { _, _ ->
                            dbListaSpese.child(idLista).child("owner").setValue(lista.partecipanti[1])

                            dbListaSpese.child(idLista).child("owner").setValue(lista.partecipanti[1])
                            lista.partecipanti.removeAt(0)
                            dbListaSpese.child(idLista).child("partecipanti").setValue(lista.partecipanti)

                            //REDIRECT MAIN PAGE
                            activity?.finish()
                            startActivity(Intent(Intent(context, MainActivity::class.java)))
                        }
                        .setNegativeButton("NO") { _, _ -> }
                        .show()
                }


                GenericUtils.setupSwitch(binding.switchSaldato, lista.isSaldato)
            }
        }.addOnFailureListener {
            Log.e(className, "<<Error getting utente", it)
        }
    }

    private fun setupSwitches(idLista: String) {
        binding.switchSaldato.setOnCheckedChangeListener { _, checkedId ->
            GenericUtils.onOffSaldato(dbListaSpese, idLista, checkedId)
        }

        dbListaSpese.child(idLista).get().addOnSuccessListener {
            if (it.exists()) {
                val lista : ListaSpese = it.getValue(ListaSpese::class.java) as ListaSpese
                GenericUtils.setupSwitch(binding.switchSaldato, lista.isSaldato)
            }
        }.addOnFailureListener {
            Log.e(className, "<<Error getting utente", it)
        }
    }

    private fun setupToolbar(nomeLista: String) {
        //Cambio il titolo della toolbar
        (activity as MainActivity).setToolbarTitle("Impostazioni $nomeLista")
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
    }
}