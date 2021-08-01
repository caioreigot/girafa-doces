package com.github.caioreigot.girafadoces.ui.main.menu

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.helper.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.model.Product
import javax.inject.Inject

class MenuAdapter @Inject constructor(
    private val resProvider: ResourcesProvider
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private lateinit var items: List<MenuItem>
    private lateinit var openOrderDialog: (product: Product) -> Unit

    /* It is vital to call this function to use this adapter */
    fun setup(items: List<MenuItem>, openOrderDialog: (product: Product) -> Unit) {
        this.items = items
        this.openOrderDialog = openOrderDialog
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val headerTV: TextView = itemView.findViewById(R.id.menu_item_header)
        private val contentTV: TextView = itemView.findViewById(R.id.menu_item_content)
        private val imageView: ImageView = itemView.findViewById(R.id.menu_item_image)

        private val bottomButtonCV: CardView = itemView.findViewById(R.id.menu_item_bottom_button_cv)
        private val bottomButtonTV: TextView = itemView.findViewById(R.id.menu_item_bottom_button_tv)

        fun bind(item: MenuItem) {
            headerTV.text = item.header
            contentTV.text = item.content
            imageView.setImageBitmap(item.image)
            bottomButtonTV.text = resProvider.getString(R.string.menu_item_order_text)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                headerTV.letterSpacing = 0.05F

            bottomButtonCV.setOnClickListener {
                openOrderDialog(Product(
                    uid = item.uid,
                    header = item.header,
                    content = item.content
                ))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}