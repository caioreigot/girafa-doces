package com.github.caioreigot.girafadoces.presentation.main.menu

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MenuItem

class MenuAdapter(
    private val items: MutableList<MenuItem>
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val header: TextView = itemView.findViewById(R.id.menu_item_header)
        private val content: TextView = itemView.findViewById(R.id.menu_item_content)
        private val imageView: ImageView = itemView.findViewById(R.id.menu_item_image)

        fun bind(item: MenuItem) {
            header.text = item.header
            content.text = item.content
            imageView.setImageBitmap(item.image)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                header.letterSpacing = 0.05F
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

}