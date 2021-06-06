package com.github.caioreigot.girafadoces.presentation.main.add

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.ResourcesProvider
import com.github.caioreigot.girafadoces.data.model.ErrorType
import com.github.caioreigot.girafadoces.data.model.FirebaseResult
import com.github.caioreigot.girafadoces.data.model.MenuItem
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.data.repository.DatabaseRepository
import com.github.caioreigot.girafadoces.data.repository.StorageRepository
import com.github.caioreigot.girafadoces.presentation.main.MainActivity

class AddAdapter(
    private val items: MutableList<MenuItem>,
    private val resProvider: ResourcesProvider,
    private val mainActivity: MainActivity,
    private val databaseSource: DatabaseRepository,
    private val storageSource: StorageRepository
) : RecyclerView.Adapter<AddAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var uid: String = ""

        private val headerTV: TextView = itemView.findViewById(R.id.menu_item_header)
        private val contentTV: TextView = itemView.findViewById(R.id.menu_item_content)
        private val imageView: ImageView = itemView.findViewById(R.id.menu_item_image)

        private val bottomButtonCV: CardView = itemView.findViewById(R.id.menu_item_bottom_button_cv)
        private val bottomButtonTV: TextView = itemView.findViewById(R.id.menu_item_bottom_button_tv)

        fun bind(item: MenuItem, position: Int) {
            uid = item.uid

            headerTV.text = item.header
            contentTV.text = item.content
            imageView.setImageBitmap(item.image)
            bottomButtonTV.text = resProvider.getString(R.string.menu_item_remove_text)

            bottomButtonCV.setOnClickListener {
                mainActivity.showMessageDialog(
                    MessageType.CONFIRMATION,
                    R.string.dialog_confirmation_title,
                    resProvider.getString(R.string.remove_menu_item_info),
                    callback = { choice ->
                        when (choice) {
                            false -> return@showMessageDialog

                            // Confirmed deletion
                            true -> removeItem(position)
                        }
                    }
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                headerTV.letterSpacing = 0.05F
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun removeItem(position: Int) {
        databaseSource.removeMenuItem(storageSource, items[position].uid) { result ->
            when (result) {
                is FirebaseResult.Success -> {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                }

                is FirebaseResult.Error -> {
                    val errorMessage = when (result.errorType) {
                        ErrorType.SERVER_ERROR ->
                            resProvider.getString(R.string.server_error_message)

                        else ->
                            resProvider.getString(R.string.unexpected_error)
                    }

                    mainActivity.showMessageDialog(
                        MessageType.ERROR,
                        R.string.dialog_error_title,
                        errorMessage
                    )
                }
            }
        }
    }

}