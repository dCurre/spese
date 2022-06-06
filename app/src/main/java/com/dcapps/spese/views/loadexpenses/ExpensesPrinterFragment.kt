package com.dcapps.spese.views.loadexpenses

import android.app.AlertDialog
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dcapps.spese.R
import com.dcapps.spese.adapters.ExpenseAdapter
import com.dcapps.spese.data.entities.ExpensesList
import com.dcapps.spese.data.viewmodels.ExpenseViewModel
import com.dcapps.spese.data.viewmodels.ExpensesListViewModel
import com.dcapps.spese.databinding.LoadSpeseTabSpeseBinding
import com.dcapps.spese.enums.bundle.BundleArgumentsEnum
import com.dcapps.spese.utils.SnackbarUtils
import com.dcapps.spese.views.dialog.EditSpesaDialogFragment
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class ExpensesPrinterFragment : Fragment(R.layout.load_spese_tab_spese) {

    companion object {
        fun newInstance(args: Bundle?): ExpensesPrinterFragment{
            val fragment = ExpensesPrinterFragment().apply{
                arguments =  args
            }
            return fragment
        }
    }

    private var _binding: LoadSpeseTabSpeseBinding? = null
    private val className = javaClass.simpleName
    private val binding get() = _binding!!
    private lateinit var expenseAdapter : ExpenseAdapter
    private lateinit var expenseViewModel : ExpenseViewModel
    private lateinit var listaSpeseModel : ExpensesListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = LoadSpeseTabSpeseBinding.inflate(inflater, container, false)
        expenseAdapter = ExpenseAdapter()
        expenseViewModel = ViewModelProvider(requireActivity())[ExpenseViewModel::class.java]
        listaSpeseModel = ViewModelProvider(this)[ExpensesListViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Stampo le spese
        printExpenses()

        //Configurazione bottone aggiunta spesa
        setupAddSpesaButton()

        //Gestisco il cardslider passandogli l'adapter
        setupCardSlider()
    }

    private fun printExpenses() {
        //Adding expenses to the adapter
        expenseViewModel.findAllByExpensesListID(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString())
        expenseViewModel.expenseListLiveData.observe(viewLifecycleOwner) { expenseList ->
            expenseAdapter.addItems(expenseList)
            binding.speseNotFound.visibility = if(expenseList.isNotEmpty()) android.view.View.INVISIBLE else android.view.View.VISIBLE
        }

        binding.listaSpese.layoutManager = LinearLayoutManager(context)
        binding.listaSpese.adapter = expenseAdapter
    }

    private fun setupScrollListener(listaSpese: RecyclerView, isPaid : Boolean) {
        val scrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //SCROLLING UP SHOWS THE BUTTON
                if (dy<0 && !binding.addSpesaButton.isShown && !isPaid){
                    binding.addSpesaButton.show()
                }

                //SCROLLING DOWN HIDES THE BUTTON
                if(dy>0 && binding.addSpesaButton.isShown){
                    binding.addSpesaButton.hide();
                }
            }
        }
        listaSpese.addOnScrollListener(scrollListener)
    }

    private fun setupCardSlider() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                //Non penso venga usato, dovrebbe uscire un alert sul movimento degli item nella lista (ma sono bloccati)
                SnackbarUtils.showSnackbarOK("ON MOVE", binding.root)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var expensesList = ExpensesList()

                listaSpeseModel.findByID(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString())
                listaSpeseModel.expensesListLiveData.observe(viewLifecycleOwner) { listaSpeseExtracted ->
                    expensesList = listaSpeseExtracted ?: ExpensesList()
                }

                if(direction == ItemTouchHelper.RIGHT){ //Se scorro verso destra modifico
                    if(!expensesList.paid){
                        EditSpesaDialogFragment().newInstance(expenseAdapter.getItem(viewHolder.absoluteAdapterPosition), expenseAdapter, viewHolder.absoluteAdapterPosition).show(childFragmentManager, EditSpesaDialogFragment.TAG)
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
                                expenseAdapter.deleteItem(viewHolder.absoluteAdapterPosition, expenseViewModel)
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

            //DRAWING BACKGROUND
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

                super.onChildDraw(c, recyclerView, viewHolder,dX / 3, dY / 3, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.listaSpese)
    }

    private fun setupAddSpesaButton() {
        listaSpeseModel.findByID(arguments?.getString(BundleArgumentsEnum.EXPENSES_LIST_ID.value).toString())
        listaSpeseModel.expensesListLiveData.observe(viewLifecycleOwner) { expensesList ->
            binding.addSpesaButton.visibility = if(expensesList?.paid == true) View.GONE else View.VISIBLE
            setupScrollListener(binding.listaSpese, expensesList?.paid == true)
            Log.i("SCROLLO", "MINCHIA SE SCROLLO")
        }

        binding.addSpesaButton.setOnClickListener{
            findNavController().navigate(
                R.id.action_loadSpeseFragment_to_addSpesaFragment,
                arguments,
                NavOptions.Builder() //ANIMATION
                    .setEnterAnim(R.anim.enter_to_top_left)
                    .setExitAnim(R.anim.exit_to_bottom_right)
                    .setPopEnterAnim(R.anim.enter_to_top_left)
                    .setPopExitAnim(R.anim.exit_to_bottom_right)
                    .build()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}