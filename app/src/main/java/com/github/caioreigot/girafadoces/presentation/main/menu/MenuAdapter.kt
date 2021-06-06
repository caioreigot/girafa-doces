package com.github.caioreigot.girafadoces.presentation.main.menu

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.MenuItem

class MenuAdapter(
    private val items: MutableList<MenuItem>,
    private val resProvider: ResourcesProvider
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val headerTV: TextView = itemView.findViewById(R.id.menu_item_header)
        private val contentTV: TextView = itemView.findViewById(R.id.menu_item_content)
        private val imageView: ImageView = itemView.findViewById(R.id.menu_item_image)
        private val bottomButtonTV: TextView = itemView.findViewById(R.id.menu_item_bottom_button_tv)

        fun bind(item: MenuItem) {
            headerTV.text = item.header
            contentTV.text = item.content
            imageView.setImageBitmap(item.image)
            bottomButtonTV.text = resProvider.getString(R.string.menu_item_order_text)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                headerTV.letterSpacing = 0.05F
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