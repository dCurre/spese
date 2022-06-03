package com.dcurreli.spese.view.loadspese

import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dcurreli.spese.R
import com.dcurreli.spese.adapters.SpesaAdapter
import com.dcurreli.spese.data.entity.ExpensesList
import com.dcurreli.spese.data.viewmodel.ExpenseViewModel
import com.dcurreli.spese.data.viewmodel.ExpensesListViewModel
import com.dcurreli.spese.databinding.LoadSpeseTabSpeseBinding
import com.dcurreli.spese.utils.SnackbarUtils
import com.dcurreli.spese.view.dialog.EditSpesaDialogFragment
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import android.view.View as View1


class TabSpeseListaSpeseFragment : Fragment(R.layout.load_spese_tab_spese) {

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
    private lateinit var expenseAdapter : SpesaAdapter
    private lateinit var expenseViewModel : ExpenseViewModel
    private lateinit var listaSpeseModel : ExpensesListViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View1 {

        _binding = LoadSpeseTabSpeseBinding.inflate(inflater, container, false)
        expenseAdapter = SpesaAdapter()
        expenseViewModel = ViewModelProvider(requireActivity())[ExpenseViewModel::class.java]
        listaSpeseModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View1, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Stampo le spese
        printSpese()

        //Gestisco il cardslider passandogli l'adapter
        setupCardSlider()
    }

    private fun printSpese() {
        //Aggiungo le spese estratte all'adapter
        expenseViewModel.findAllByExpensesListID(arguments?.getString("idLista").toString())
        expenseViewModel.expenseListLiveData.observe(viewLifecycleOwner) { expenseList ->
            //TODO CAMBIARE CON UNA QUERY SERIA
            expenseAdapter.addItems(expenseList.sortedBy { it.expenseDateTimestamp }.toCollection(ArrayList()))
            binding.speseNotFound.visibility = if(expenseList.isNotEmpty()) android.view.View.INVISIBLE else android.view.View.VISIBLE
        }

        //Setup spese
        binding.listaSpese.layoutManager = LinearLayoutManager(context)
        binding.listaSpese.adapter = expenseAdapter
    }

    private fun setupCardSlider() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    //Non penso venga usato, dovrebbe uscire un alert sul movimento degli item nella lista (ma sono bloccati)
                    SnackbarUtils.showSnackbarOK("ON MOVE", binding.root)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    var expensesList = ExpensesList(null, null, null, null, false, null)

                    listaSpeseModel.findByID(arguments?.getString("idLista").toString())
                    listaSpeseModel.expensesListLiveData.observe(viewLifecycleOwner) { listaSpeseExtracted ->
                        expensesList = listaSpeseExtracted
                    }

                    if(direction == ItemTouchHelper.RIGHT){ //Se scorro verso destra modifico
                        if(!expensesList.paid){
                            EditSpesaDialogFragment().newInstance(expenseAdapter.getItem(viewHolder.absoluteAdapterPosition)).show(childFragmentManager, EditSpesaDialogFragment.TAG)
                            expenseAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                        } else {
                            SnackbarUtils.showSnackbarError("Non puoi modificare una spesa se la lista è saldata!", binding.loadSpeseTabConstraintLayout)
                            expenseAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                        }
                    }

                    if(direction == ItemTouchHelper.LEFT){ //Se scorro verso sinistra cancello
                        if(!expensesList.paid){
                            //Se non è saldato faccio uscire l'alert per la cancellazione della spesa
                            AlertDialog.Builder(context)
                                .setTitle("Conferma")
                                .setMessage("Vuoi cancellare la spesa ${expenseAdapter.getItem(viewHolder.absoluteAdapterPosition).expense}?")
                                .setPositiveButton("SI") { _, _ ->
                                    expenseViewModel.delete(expenseAdapter.getItem(viewHolder.absoluteAdapterPosition).id)
                                    SnackbarUtils.showSnackbarOK("Spesa cancellata", binding.root)
                                }
                                .setNegativeButton("NO") { _, _ ->
                                    expenseAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                                }
                                .setCancelable(false)
                                .show()
                        } else {
                            SnackbarUtils.showSnackbarError("Non puoi cancellare una spesa se la lista è saldata!", binding.loadSpeseTabConstraintLayout)
                            expenseAdapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                        }
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