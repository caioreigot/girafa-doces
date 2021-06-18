package com.github.caioreigot.girafadoces.presentation.main.admin.administrators

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.*
import com.github.caioreigot.girafadoces.presentation.main.admin.AdminViewModel

class AdministratorsAdapter(
    private val adminViewModel: AdminViewModel,
    private val resProvider: ResourcesProvider,
    val items: MutableList<User>
) : RecyclerView.Adapter<AdministratorsAdapter.AdministratorsViewHolder>() {

    inner class AdministratorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val adminHeader: TextView = itemView.findViewById(R.id.administrators_item_header)
        private val adminFullNameTV: TextView = itemView.findViewById(R.id.admininistrators_item_full_name_tv)
        private val informationsAdminBtn: Button = itemView.findViewById(R.id.administrators_item_informations_btn)
        private val removeAdminBtn: Button = itemView.findViewById(R.id.administrators_item_remove_btn)

        fun bind(admin: User, position: Int) {
            if (admin.email == UserSingleton.email) {
                adminHeader.text = resProvider.getString(R.string.administrator_you_item_header)

                informationsAdminBtn.visibility = View.GONE
                removeAdminBtn.visibility = View.GONE
            }

            adminFullNameTV.text = admin.fullName

            removeAdminBtn.setOnClickListener {
                adminViewModel.removeAdmin(admin.email, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdministratorsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.administrators_item, parent, false)
        return AdministratorsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdministratorsViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}