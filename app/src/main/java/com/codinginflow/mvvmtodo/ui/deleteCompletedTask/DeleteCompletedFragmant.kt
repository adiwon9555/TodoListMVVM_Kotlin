package com.codinginflow.mvvmtodo.ui.deleteCompletedTask

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint


//In navGraph we have set this fragmant to be accessible from global
@AndroidEntryPoint
//Extending DialogFragment will auto generate UI of dialog box, hence this fragmant can be a reusable dialog
class DeleteCompletedFragmant : DialogFragment() {

    private val viewModel: DeleteCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Do you really want to delete all Completed Tasks?")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Yes"){ _,_ ->
                viewModel.deleteAllCompletedTask()
            }
            .create()
}