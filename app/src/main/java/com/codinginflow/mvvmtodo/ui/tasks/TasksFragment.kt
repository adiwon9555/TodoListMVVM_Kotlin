package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmanTasksBinding
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding
import com.codinginflow.mvvmtodo.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//Adds DI container to the class
@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragman_tasks) {

    //by viewModels() is property delegate and using this way also will inject dependency as we have used @AndroidEntryPoint
    //TODO: Read property delegate
    private val viewModel: TaskViewModel by viewModels()

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
        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            taskAdapter.submitList(it)
        })
        //2nd parameter of observe is a lambda, which can be written in this way ( sort of an inner class or callback)

        //To show menu in this fragmant
        setHasOptionsMenu(true)
    }

    //For menu on topbar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //Inflaing and adding menu for our fragmant
        //Note:- data bindin is available only for layout files, hence we reference with id here
        inflater.inflate(R.menu.fragmant_task_menu, menu)
        val searchItem = menu.findItem(R.id.search_task_icon)

        //as is casting the view to SearchView
        //actionView for search item is the view that is shown as textbox when searchicon is clicked
        //kotlin has shorthand syntax for getter and setter which can be directly called with the proerty name
        val searchView = searchItem.actionView as SearchView

        //Below we can write directy write what we want to do when a text is typed in searchView but, we will create a util to do this
        //This is the funciton we create in utils that is expecting a listener lambda function
        //So we trailing lambda syntax that can be written without parenthesis
        //Note:- Writing return normally in lambda function is allowed but since we have used inline with crossinline keyword, so not allowed
        searchView.onQueryTextChanged {
            //setting the searchVal in viewModel that is trigger a query on room table
            //Which will emit new flow of data and collected a live data in recyclerview
            viewModel.searchVal.value = it
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //when is switch case of kotlin
        return when (item.getItemId()) {
            R.id.sort_by_name_menu -> {
                //aas return is written above, here we can only write true
                //true means task is done, giving false means let system handle default action
                viewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }

            R.id.sort_by_date_menu -> {
                viewModel.sortOrder.value = SortOrder.BY_DATE
                true
            }
            R.id.hide_completed_menu -> {
                item.isChecked = !item.isChecked
                viewModel.hideComleted.value = item.isChecked
                true
            }

            R.id.delete_all_menu -> {
                true
            }

            else ->
                super.onOptionsItemSelected(item)
        }
    }
}