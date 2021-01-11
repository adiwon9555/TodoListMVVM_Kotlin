package com.codinginflow.mvvmtodo.ui.tasks

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.FragmanTasksBinding
import com.codinginflow.mvvmtodo.utils.exhaustive
import com.codinginflow.mvvmtodo.utils.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragman_tasks.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//Adds DI container to the class
@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragman_tasks), TaskAdapter.onItemClickListener {

    //by viewModels() is property delegate and using this way also will inject dependency as we have used @AndroidEntryPoint
    //TODO: Read property delegate
    private val viewModel: TaskViewModel by viewModels()

    //is called when layout was instantiated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val binding = FragmanTasksBinding.bind(view)
        //Since our class is implementing the TaskAdapter.onItemClickListener, we can pass this here
        val taskAdapter = TaskAdapter(this)

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
            //For swipe gesture control
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0
                ,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                //for drag and drop
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwipeToDelete(task);
                }
            }).attachToRecyclerView(recyclerViewTask)
            
            fab_add_task.setOnClickListener{
                viewModel.onAddNewTaskClick()
            }
        }

        //To recieve the data passed from other fragmant
        setFragmentResultListener("add_edit_request"){_,bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.addEditTaskResult(result)
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

        //using launchWhenStarted to shorten the scope more that starts only when view is started
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { events ->
                when(events){
                    is TaskViewModel.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(),"Task Deleted",Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                                //smart cast
                                //Again giving control to viewModel to handle login
                                viewModel.onUndoDeleteTask(events.task)
                            }.show()
                    }
                    is TaskViewModel.TasksEvent.NavigateToEditTaskScreen -> {
                        //Need to Rebuild project to make this class available
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(events.task,"Edit Task")
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TasksEvent.NavigateToAddTaskScreen -> {
                        //Need to Rebuild project to make this class available
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(null,"New Task")
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(),events.message,Snackbar.LENGTH_SHORT).show()
                    }
                    TaskViewModel.TasksEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action = TasksFragmentDirections.actionGlobalDeleteCompletedFragmant()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        //TODO:Why is this not possible
//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.deletedTask.collect { task ->
//                if(task.id != -1){
//                    Snackbar.make(requireView(),"Task Deleted",Snackbar.LENGTH_LONG)
//                        .setAction("UNDO"){
//                            //smart cast
//                            //Again giving control to viewModel to handle login
//                            viewModel.onUndoDeleteTask(task)
//                        }.show()
//                }
//
//            }
//        }

        
        


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
        //On create we need to set the checked value of hideCompleted as according to saved value in datastore preference
        //Filtered sorted data is always returned with datastore preference.
        viewLifecycleOwner.lifecycleScope.launch {
            //first collects only once and does not keep listening
            menu.findItem(R.id.hide_completed_menu).isChecked =
                viewModel.preferencesFLow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //when is switch case of kotlin
        return when (item.getItemId()) {
            R.id.sort_by_name_menu -> {
                //aas return is written above, here we can only write true
                //true means task is done, giving false means let system handle default action
//                viewModel.sortOrder.value = SortOrder.BY_NAME
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

            R.id.sort_by_date_menu -> {
//                viewModel.sortOrder.value = SortOrder.BY_DATE
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.hide_completed_menu -> {
                item.isChecked = !item.isChecked
//                viewModel.hideComleted.value = item.isChecked
                viewModel.onHideCompletedChecked(item.isChecked)
                true

            }

            R.id.delete_all_menu -> {
                viewModel.onDeleteAllCompletedClick()
                true
            }

            else ->
                super.onOptionsItemSelected(item)
        }
    }


    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task, isChecked)
    }
}