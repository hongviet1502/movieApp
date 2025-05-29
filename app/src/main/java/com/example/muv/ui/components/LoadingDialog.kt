package com.example.muv.ui.components

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.muv.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoadingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.dialog_loading)
            .setCancelable(false)
            .create()
    }
}