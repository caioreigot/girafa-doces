package com.github.caioreigot.girafadoces.presentation.main.menu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MenuItem

class MenuFragment : Fragment() {

    lateinit var menuRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuRecyclerView = view.findViewById(R.id.menu_recycler_view)
        menuRecyclerView.adapter = MenuAdapter(createFakeItems(requireContext()))
        menuRecyclerView.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(menuRecyclerView)
    }

    fun createFakeItems(context: Context): MutableList<MenuItem> {
        return mutableListOf(
            MenuItem(
                header = "BOLO DE POTE",
                content = "NINHO/ R$ 7,00\nBRIGADEIRO/ R$ 7,00\nPRESTÍGIO/ R$ 7,00",
                image = ContextCompat.getDrawable(context, R.drawable.bolo)!!
            ),
            MenuItem(
                header = "BOLO DE POTE",
                content = "NINHO/ R$ 7,00\nBRIGADEIRO/ R$ 7,00\nPRESTÍGIO/ R$ 7,00",
                image = ContextCompat.getDrawable(context, R.drawable.bolo)!!
            ),
            MenuItem(
                header = "BOLO DE POTE",
                content = "NINHO/ R$ 7,00\nBRIGADEIRO/ R$ 7,00\nPRESTÍGIO/ R$ 7,00",
                image = ContextCompat.getDrawable(context, R.drawable.bolo)!!
            ),
            MenuItem(
                header = "BOLO DE POTE",
                content = "NINHO/ R$ 7,00\nBRIGADEIRO/ R$ 7,00\nPRESTÍGIO/ R$ 7,00",
                image = ContextCompat.getDrawable(context, R.drawable.bolo)!!
            ),
            MenuItem(
                header = "BOLO DE POTE",
                content = "NINHO/ R$ 7,00\nBRIGADEIRO/ R$ 7,00\nPRESTÍGIO/ R$ 7,00",
                image = ContextCompat.getDrawable(context, R.drawable.bolo)!!
            )
        )
    }
}