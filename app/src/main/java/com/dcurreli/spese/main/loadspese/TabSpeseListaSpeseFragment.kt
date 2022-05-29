package com.dcurreli.spese.main.loadspese

import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.SpesaAdapter
import com.dcurreli.spese.databinding.LoadSpeseTabSpeseBinding
import com.dcurreli.spese.enum.TablesEnum
import com.dcurreli.spese.main.dialog.EditSpesaDialogFragment
import com.dcurreli.spese.objects.ListaSpese
import com.dcurreli.spese.utils.DBUtils
import com.dcurreli.spese.utils.SnackbarUtils
import com.dcurreli.spese.utils.SpesaUtils
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import android.view.View as View1


class TabSpeseListaSpeseFragment : Fragment(R.layout.load_spese_tab_spese) {

    private var dbListe = DBUtils.getDatabaseReference(TablesEnum.LISTE)
    private var dbSpesa = DBUtils.getDatabaseReference(TablesEnum.SPESA)

    companion object {
        fun newInstance(args: Bundle?): TabSpeseListaSpeseFragment{
            val fragment = TabSpeseListaSpeseFragment().apply{
                arguments =  args
            }
            return fragment
        }
    }

    private var _binding: LoadSpeseTabSpeseBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = LoadSpeseTabSpeseBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Stampo le spese
        val spesaAdapter = SpesaUtils.printSpese(
            binding,
            requireContext(),
            idListaSpese = arguments?.getString("idLista").toString(), //id lista
        )

        //Gestisco il cardslider passandogli l'adapter
        setupCardSlider(spesaAdapter)
    }

    private fun setupCardSlider(spesaAdapter: SpesaAdapter) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    //Non penso venga usato, dovrebbe uscire un alert sul movimento degli item nella lista (ma sono bloccati)
                    SnackbarUtils.showSnackbarOK("ON MOVE", binding.root)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    dbListe.child(arguments?.getString("idLista").toString()).get().addOnSuccessListener {
                        val listaSpese = it.getValue(ListaSpese::class.java) as ListaSpese
                            if(direction == ItemTouchHelper.RIGHT){  //Se scorro verso destra modifico
                                if(!listaSpese.isSaldato){
                                    //TODO dialog edit
                                    EditSpesaDialogFragment().newInstance(spesaAdapter.getItem(viewHolder.absoluteAdapterPosition)).show(childFragmentManager, EditSpesaDialogFragment.TAG)

                                    //SnackbarUtils.showSnackbarOK("Spesa modificata", binding.root)
                                    spesaAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                                } else {
                                    SnackbarUtils.showSnackbarError("Non puoi modificare una spesa se la lista è saldata!", binding.loadSpeseTabConstraintLayout)
                                    spesaAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                                }
                            }

                            if(direction == ItemTouchHelper.LEFT){ //Se scorro verso sinistra cancello
                                //Se non è saldato faccio uscire l'alert per la cancellazione della spesa
                                if(!listaSpese.isSaldato){
                                    //Se non è saldato faccio uscire l'alert per la cancellazione della spesa
                                    AlertDialog.Builder(context)
                                    .setTitle("Conferma")
                                    .setMessage("Vuoi cancellare la spesa ${spesaAdapter.getItem(viewHolder.absoluteAdapterPosition).spesa}?")
                                    .setPositiveButton("SI") { _, _ ->
                                        spesaAdapter.getItem(viewHolder.absoluteAdapterPosition).delete()
                                        SnackbarUtils.showSnackbarOK("Spesa cancellata", binding.root)
                                    }
                                    .setNegativeButton("NO") { _, _ ->
                                        spesaAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                                    }
                                    .setCancelable(false)
                                    .show()
                                } else {
                                    SnackbarUtils.showSnackbarError("Non puoi cancellare una spesa se la lista è saldata!", binding.loadSpeseTabConstraintLayout)
                                    spesaAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                                }
                            }
                    }.addOnFailureListener {
                        Log.e(className, "<<Error getting utente", it)
                    }
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            //LEFT
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                        .addSwipeLeftLabel("CANCELLA")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(requireContext(), R.color.onPrimary))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                            //RIGHT
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                        .addSwipeRightLabel("MODIFICA")
                        .setSwipeRightLabelColor(ContextCompat.getColor(requireContext(), R.color.onPrimary))
                        .addSwipeRightActionIcon(R.drawable.ic_pencil_edit)
                            //BOTH
                        .setActionIconTint(ContextCompat.getColor(requireContext(), R.color.onPrimary))
                        .create()
                        .decorate()

                    super.onChildDraw(c, recyclerView, viewHolder,dX, dY, actionState, isCurrentlyActive)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.listaSpese)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}