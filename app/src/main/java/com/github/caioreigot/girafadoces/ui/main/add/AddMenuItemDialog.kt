package com.github.caioreigot.girafadoces.ui.main.add

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.ui.main.MainActivity
import java.io.ByteArrayOutputStream

class AddMenuItemDialog(
    private val addViewModel: AddViewModel,
    private val clearRecyclerView: () -> Unit
) : DialogFragment() {

    val PICK_IMAGE = 1

    val VIEW_FLIPPER_BUTTON = 0
    val VIEW_FLIPPER_PROGRESS_BAR = 1

    lateinit var contentET: EditText
    lateinit var titleET: EditText
    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar

    lateinit var viewFlipper: ViewFlipper

    lateinit var addDialogBtnCV: CardView
    lateinit var chooseImageBtnCV: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_menu_item_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        titleET = view.findViewById(R.id.menu_item_header)
        contentET = view.findViewById(R.id.dialog_menu_item_content)
        imageView = view.findViewById(R.id.menu_item_image)
        progressBar = view.findViewById(R.id.add_dialog_progress_bar)

        viewFlipper = view.findViewById(R.id.add_dialog_view_flipper)

        addDialogBtnCV = view.findViewById(R.id.dialog_add_btn_cv)
        chooseImageBtnCV = view.findViewById(R.id.choose_image_cardview)

        chooseImageBtnCV.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecionar Imagem"), PICK_IMAGE)
        }

        addDialogBtnCV.setOnClickListener {
            if (imageView.drawable == null) {
                (activity as MainActivity).showMessageDialog(
                    MessageType.ERROR,
                    R.string.dialog_error_title,
                    "Nenhuma imagem selecionada",
                    null
                )
            } else {
                val imageBitmap = (imageView.drawable as BitmapDrawable).bitmap
                val imageInByte = convertBitmapToArrayBite(imageBitmap)

                addViewModel.saveMenuItemDatabase(
                    titleET.text.toString(),
                    contentET.text.toString(),
                    imageInByte
                ) { closeDialog ->
                    when (closeDialog) {
                        // Clear and update RecyclerView
                        true -> {
                            clearRecyclerView()
                            addViewModel.getMenuItems()
                            dismiss()
                        }

                        // Hide progress bar and show button
                        false -> viewFlipper.displayedChild = VIEW_FLIPPER_BUTTON
                    }
                }

                viewFlipper.displayedChild = VIEW_FLIPPER_PROGRESS_BAR
            }
        }
    }

    private fun convertBitmapToArrayBite(imageBitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data?.data)
            chooseImageBtnCV.visibility = View.GONE
        }
    }
}