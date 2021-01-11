package com.codinginflow.mvvmtodo.ui.addEditTask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    //savedInstanceState is the only object that can escape process death (When all app's component are auto killed due to in background for long time)
    //This savedInstanceState is available as an argument in fragmants onViewCreated and can be passed from there to be used
    //But we have a better way to just use SavedStateHandle injected to our viewModel that has the same reference
    //To inject this we need @Assisted as the dagger handled the stitching of savedInstanceState to SavedStateHandle
    @Assisted private val state : SavedStateHandle
) : ViewModel() {

    //We are using SavedStateHandle state to keep track of the data that is populated in addEditFragmant
    //By default the args passed to a fragmant is already stored inside the SavedStateHandle so we dont need to manually do that
    val task = state.get<Task>("task")
    //Now we want to manually store each values inside state so that it is persisted and escapes process death
    //In Kotlin a property consist of the field,getter and setter and we can override that
    var taskName = state.get<String>("taskName") ?: task?.taskName ?: ""
        set(value) {
            field = value
            state.set("taskName",value)
        }
    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance",value)
        }
    //We are not setting date to state as user can not change that and is always the same value as received as property of task argument.
    //So now even if user Types something as TaskName and goes away, when he comes back it will still be there

    private val addEditTaskEventChannel = Channel<AddEditTaskEvents>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        //Invalid Input case
        if(taskName.isBlank()){
            showInvalidInputMessage("Task name can not be empty")
            return
        }
        //For Edit else add
        if(task != null){
            val task = task.copy(taskName = taskName,important = taskImportance)
            updateTask(task)
        }else{
            val task = Task(taskName = taskName,important = taskImportance)
            createTask(task)
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch{
        taskDao.insert(task)
        //Now we need to show message in the task Fragmant that the operation was successfull, as addEditTask view will be popped as soon as operation is completed
        addEditTaskEventChannel.send(AddEditTaskEvents.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch{
        taskDao.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvents.NavigateBackWithResult(EDIT_TASK_RESULT_OK))

    }

    private fun showInvalidInputMessage(s: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvents.ShowInvalidInputMessage(s))

    }

    sealed class AddEditTaskEvents{
        data class ShowInvalidInputMessage(val message : String) : AddEditTaskEvents()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvents()
    }


}