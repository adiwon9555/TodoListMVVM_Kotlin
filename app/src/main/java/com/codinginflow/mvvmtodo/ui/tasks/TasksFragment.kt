package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmanTasksBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//Adds DI container to the class
@AndroidEntryPoint
class TasksFragment: Fragment(R.layout.fragman_tasks) {

    //by viewModels() is property delegate and using this way also will inject dependency as we have used @AndroidEntryPoint
    //TODO: Read property delegate
    private val viewModel : TaskViewModel by viewModels()

    //is called when layout was instantiated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val binding = FragmanTasksBinding.bind(view)

        val taskAdapter = TaskAdapter()

        binding.apply {
            recyclerViewTask.apply {
                adapter = taskAdapter
                //requireContext is a fragmant method that gets us the current context
                //layoutManager is responsinsible in arranging the list of recyclerView is particular way
                layoutManager = LinearLayoutManager(requireContext())
                //Telling the size does not dynamically change improving performance
                //TODO: CHECK HOW THIS IS DONE
                setHasFixedSize(true)
            }
        }

        //first parameter of observe is which lifecycle we want the livedata to be aware of
        //Fragmant has two lifecycle - lifecycle of its object and its view
        //on moving to new fragmant, current fragmant object is put to backstack and only view is destroyed
        //here we want the viewLifecycle always because as soon as View is destroyed in case to rotate or navigate, livedata stops event dispatching
        viewModel.tasks.observe(viewLifecycleOwner , Observer{
            taskAdapter.submitList(it)
        })
        //2nd parameter of observe is a lambda, which can be written in this way ( sort of an inner class or callback)
    }
}