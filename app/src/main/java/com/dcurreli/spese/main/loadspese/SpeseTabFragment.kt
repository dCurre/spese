package com.dcurreli.spese.main.loadspese

import android.app.Dialog
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
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
import com.dcurreli.spese.utils.GenericUtils
import com.dcurreli.spese.utils.SpesaUtils
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import android.view.View as View1


class SpeseTabFragment() : Fragment(R.layout.load_spese_tab_spese) {

    companion object {
        fun newInstance(args: Bundle?): SpeseTabFragment{
            val fragment = SpeseTabFragment().apply{
                arguments =  args
            }
            return fragment
        }
    }

    private var _binding: LoadSpeseTabSpeseBinding? = null
    private val TAG = javaClass.simpleName
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
            activity
        )

        //Gestisco il cardslider passandogli l'adapter
        setupCardSlider(spesaAdapter) //TODO blocco swipe e riposizionamento item se non modifico o cancello
    }

    private fun setupCardSlider(spesaAdapter: SpesaAdapter) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if(direction == ItemTouchHelper.RIGHT){  //Se scorro verso destra modifico
                    }

                    if(direction == ItemTouchHelper.LEFT){ //Se scorro verso sinistra cancello
                        //TODO confirm dialog per delete

                        GenericUtils.showSnackbarOK("SWIPE VERSO DESTRA", binding.root)
                        //spesaAdapter.getItem(viewHolder.adapterPosition).delete()
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

                    GenericUtils.showSnackbarOK("SWIPE VERSO DESTRA ${dX}", binding.root)

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.listaSpese)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.edit_spesa_dialog)
        dialog.show()
    }

}