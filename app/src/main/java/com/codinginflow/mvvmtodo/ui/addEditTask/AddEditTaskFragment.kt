package com.codinginflow.mvvmtodo.ui.addEditTask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmantAddEditTaskBinding
import com.codinginflow.mvvmtodo.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragmant_add_edit_task) {
    private val viewModel : AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmantAddEditTaskBinding.bind(view)
        binding.apply {
            editTextTaskName.setText(viewModel.taskName)
            checkboxSetImportant.isChecked = viewModel.taskImportance
            checkboxSetImportant.jumpDrawablesToCurrentState()
            createdDateTextView.isVisible = viewModel.task != null
            createdDateTextView.text = "Created at: ${viewModel.task?.createdDateFormat}"

            //Polulating values in viewModel to the savedInstanceState
            editTextTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            checkboxSetImportant.setOnCheckedChangeListener{_,isChecked ->
                viewModel.taskImportance = isChecked
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }

        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect {
                when(it){
                    is AddEditTaskViewModel.AddEditTaskEvents.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(),it.message,Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvents.NavigateBackWithResult -> {
                        binding.editTextTaskName.clearFocus() //to remove keyboard ui
                        //Sending data to taskfragmant to what to do
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to it.result)
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }


}