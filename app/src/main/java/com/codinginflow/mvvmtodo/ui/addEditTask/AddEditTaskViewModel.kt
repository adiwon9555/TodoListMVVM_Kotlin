package com.codinginflow.mvvmtodo.ui.addEditTask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao

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
}