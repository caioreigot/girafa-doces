package com.github.caioreigot.girafadoces.ui.main.admin.administrators

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.*
import com.github.caioreigot.girafadoces.ui.main.admin.AdminPanelViewModel

class AdministratorsAdapter(
    private val adminPanelViewModel: AdminPanelViewModel,
    private val resProvider: ResourcesProvider,
    val items: MutableList<User>
) : RecyclerView.Adapter<AdministratorsAdapter.AdministratorsViewHolder>() {

    inner class AdministratorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val adminHeader: TextView = itemView.findViewById(R.id.administrators_item_header)
        private val adminFullNameTV: TextView = itemView.findViewById(R.id.admininistrators_item_full_name_tv)
        private val informationAdminBtn: CardView = itemView.findViewById(R.id.administrators_item_information_btn_cv)
        private val removeAdminBtn: CardView = itemView.findViewById(R.id.administrators_item_remove_btn_cv)

        fun bind(admin: User, position: Int) {
            if (admin.email == UserSingleton.email) {
                adminHeader.text = resProvider.getString(R.string.administrator_you_item_header)

                informationAdminBtn.visibility = View.GONE
                removeAdminBtn.visibility = View.GONE
            }

            adminFullNameTV.text = admin.fullName

            removeAdminBtn.setOnClickListener {
                adminPanelViewModel.removeAdmin(admin.email, position)
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