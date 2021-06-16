package com.github.caioreigot.girafadoces.presentation.main.admin.administrators

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.User

class AdministratorsAdapter(private val items: MutableList<User>) :
    RecyclerView.Adapter<AdministratorsAdapter.AdministratorsViewHolder>() {

    inner class AdministratorsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val adminFullNameTV: TextView = itemView.findViewById(R.id.admininistrators_item_full_name_tv)

        fun bind(admin: User) {
            adminFullNameTV.text = admin.fullName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdministratorsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.administrators_item, parent, false)
        return AdministratorsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdministratorsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}