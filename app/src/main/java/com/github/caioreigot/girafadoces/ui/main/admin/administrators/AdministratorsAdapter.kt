package com.github.caioreigot.girafadoces.ui.main.admin.administrators

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.*
import javax.inject.Inject

class AdministratorsAdapter @Inject constructor(
    private val resProvider: ResourcesProvider
) : RecyclerView.Adapter<AdministratorsAdapter.AdministratorsViewHolder>() {

    lateinit var items: MutableList<User>
    lateinit var fragmentManager: FragmentManager
    lateinit var removeAdmin: (adminEmail: String, position: Int) -> Unit

    /* It is vital to call this function to use this adapter */
    fun setup(
        items: MutableList<User>,
        fragmentManager: FragmentManager,
        removeAdmin: (adminEmail: String, position: Int) -> Unit
    ) {
        this.items = items
        this.fragmentManager = fragmentManager
        this.removeAdmin = removeAdmin
    }

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

            informationAdminBtn.setOnClickListener {
                val adminInfoDialog = AdminInfoDialog(admin)
                adminInfoDialog.show(fragmentManager, adminInfoDialog.tag)
            }

            removeAdminBtn.setOnClickListener {
                removeAdmin(admin.email, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdministratorsViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.administrators_item, parent, false)

        return AdministratorsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdministratorsViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}