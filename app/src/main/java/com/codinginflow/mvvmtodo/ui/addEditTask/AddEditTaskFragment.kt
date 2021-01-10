package com.codinginflow.mvvmtodo.ui.addEditTask

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmantAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragmant_add_edit_task) {
    private val viewModel : AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmantAddEditTaskBinding.bind(view)
        binding.apply {
            editTextTaskName.setText(viewModel.taskName)
            checkboxSetImportant.isChecked = viewModel.taskImportance
            createdDateTextView.isVisible = viewModel.task != null
            createdDateTextView.text = "Created at: ${viewModel.task?.createdDateFormat}"
        }
    }


}