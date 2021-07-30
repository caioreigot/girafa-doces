package com.github.caioreigot.girafadoces.ui.main.menu.admin_menu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.github.caioreigot.girafadoces.R
import com.github.caioreigot.girafadoces.data.model.MessageType
import com.github.caioreigot.girafadoces.ui.main.BottomNavActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class AdminMenuItemDialog(
    private val clearRecyclerView: () -> Unit
) : DialogFragment(R.layout.dialog_add_menu_item) {

    @Inject
    lateinit var adminMenuVMFactory: AdminMenuViewModel.Factory

    private val adminMenuViewModel: AdminMenuViewModel by viewModels(
        { requireParentFragment() },
        { adminMenuVMFactory }
    )

    private lateinit var contentET: EditText
    private lateinit var titleET: EditText
    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var percentageProgressTV: TextView

    private lateinit var viewFlipper: ViewFlipper

    private lateinit var addDialogBtnCV: CardView
    private lateinit var chooseImageBtnCV: CardView

    companion object {
        private const val PICK_IMAGE = 1

        private const val VIEW_FLIPPER_BUTTON = 0
        private const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        titleET = view.findViewById(R.id.menu_item_header)
        contentET = view.findViewById(R.id.dialog_menu_item_content)
        imageView = view.findViewById(R.id.menu_item_image)
        progressBar = view.findViewById(R.id.add_dialog_progress_bar)
        percentageProgressTV = view.findViewById(R.id.progress_percentage_tv)

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
                (activity as BottomNavActivity).showMessageDialog(
                    MessageType.ERROR,
                    R.string.dialog_error_title,
                    "Nenhuma imagem selecionada",
                    null
                )
            } else {
                val imageBitmap = (imageView.drawable as BitmapDrawable).bitmap
                val imageInByte = convertBitmapToArrayBite(imageBitmap)

                adminMenuViewModel.saveMenuItemDatabase(
                    titleET.text.toString(),
                    contentET.text.toString(),
                    imageInByte
                ) { closeDialog ->
                    when (closeDialog) {
                        // Clear and update RecyclerView
                        true -> {
                            clearRecyclerView()
                            adminMenuViewModel.getMenuItems()
                            dismiss()
                        }

                        // Hide progress bar and show button
                        false -> viewFlipper.displayedChild = VIEW_FLIPPER_BUTTON
                    }
                }

                viewFlipper.displayedChild = VIEW_FLIPPER_PROGRESS_BAR
            }
        }

        adminMenuViewModel.uploadProgress.observe(viewLifecycleOwner, {
            it?.let { percentageProgress ->
                percentageProgressTV.text = "${percentageProgress}%"
                progressBar.progress = percentageProgress
            }
        })
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