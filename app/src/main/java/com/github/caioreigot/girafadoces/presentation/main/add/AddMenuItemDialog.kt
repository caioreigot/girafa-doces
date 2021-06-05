package com.github.caioreigot.girafadoces.presentation.main.add

import android.R.attr.bitmap
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.github.caioreigot.girafadoces.R
import java.io.ByteArrayOutputStream


class AddMenuItemDialog(private val addViewModel: AddViewModel) : DialogFragment() {

    val PICK_IMAGE = 1

    lateinit var contentET: EditText
    lateinit var titleET: EditText
    lateinit var imageView: ImageView
    lateinit var percentageTV: TextView

    lateinit var addDialogBtn: Button
    lateinit var chooseImageBtn: Button

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
        percentageTV = view.findViewById(R.id.add_dialog_percentage_tv)

        addDialogBtn = view.findViewById(R.id.add_btn_dialog)
        chooseImageBtn = view.findViewById(R.id.choose_image_btn)

        chooseImageBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecionar Imagem"), PICK_IMAGE)
        }

        addDialogBtn.setOnClickListener {
            if (imageView.drawable == null) {
                Toast.makeText(
                    requireContext(),
                    "Nenhuma imagem selecionada!",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            val imageBitmap = (imageView.drawable as BitmapDrawable).bitmap
            val imageInByte = convertBitmapToArrayBite(imageBitmap)

            addViewModel.saveMenuItemDatabase(
                titleET.text.toString(),
                contentET.text.toString(),
                imageInByte
            )

            addDialogBtn.visibility = View.GONE
            percentageTV.visibility = View.VISIBLE
        }
    }

    private fun convertBitmapToArrayBite(imageBitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE) {
            imageView.setImageURI(data?.data)
            chooseImageBtn.visibility = View.GONE
        } else
            Toast.makeText(
                requireContext(),
                "Você não escolheu uma imagem",
                Toast.LENGTH_LONG
            ).show()
    }
}