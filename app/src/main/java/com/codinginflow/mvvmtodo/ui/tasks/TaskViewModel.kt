package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

//For ViewModel we use @ViewModelInject to tell hilt how to provide an instance of Architecture component ViewModel
class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {
    //asLivedata  converts flow to livedata, conventional practice as in view layer we use LiveData because
    //Livedata makes handling lifecycle of data easier as it is lifecycle aware
    //Meaning when fragmant goes in background and becomes inactive, livedata auto detects and stops dispatching events avoiding memory leaks and crashes
    //Flow on the other hand is used below viewModel as its flexible and has operators to work as intermediaries in converting our data as we want.
    //Also Since with Flow we can switch thread

    //MutablestateFLow is a flow that is listening for new data (HERE ->  from the UI as producer )
    //passing empty string as default init value
//    val searchVal = MutableStateFlow("")

    //We will make seach val to be stored in SavedStateHandle ( savedStateInstace) so that the search value is persisted
    //We make to livedata as converting to flow is easier and also we dont need to set the state with latest value, as it updates itself
    val searchVal = state.getLiveData<String>("searchQuery","")

//    val sortOrder = MutableStateFlow(BY_DATE)
//    val hideComleted = MutableStateFlow(false)

    //flatMapLatest is an flow opearator that gets latest value from the flow
//    val searchedTaskFlow = searchVal.flatMapLatest {
//        taskDao.getAllTask(it)
//    }

    //Now since we have three flows, so we need to listen is any flow updates, we do this with combine
    //combine always latest values of all flows, if any changes
    //last argument of combine is a lambda where we can perform task with latest data or return it
    //since we can return only one value, so use Triple class to return three values
    //these three values are destructured and recieved in flatMapLatest to call dao and get the filtered list
//    val searchedTaskFlow = combine(
//        searchVal, sortOrder, hideComleted
//    ) { searchVal, sortOrder, hideCompleted ->
//        Triple(searchVal, sortOrder, hideCompleted)
//    }.flatMapLatest {(searchVal, sortOrder, hideCompleted) ->
//        taskDao.getTasks(searchVal, sortOrder, hideCompleted)
//    }

    //Better than MutableStateFlow, we should use jetpack datastore that can store data even when app is closed.
    //We dont use room because it is for only large and structured data

    //Creating channel to transmit data between coroutines ( Here viewModelScope and fragmant lifecyclescope

    //Why not possible ?
//    val deletedTask = MutableStateFlow(Task("default",id = -1))

    private val taskEventChannel = Channel<TasksEvent>()
    //we want this channel to only transmit hence we are exposing only flow to recieve
    val tasksEvent = taskEventChannel.receiveAsFlow()
    val preferencesFLow = preferencesManager.preferencesFLow

    val searchedTaskFlow = combine(
        searchVal.asFlow(),preferencesFLow
    ){ searchVal, preferencesFLow ->
        Pair(searchVal,preferencesFLow)
    }.flatMapLatest {(searchVal, preferencesFLow)  ->
        taskDao.getTasks(searchVal, preferencesFLow.sortOrder, preferencesFLow.hideCompleted)
    }

    val tasks = searchedTaskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder){
        //we use the scope of viewmodel to launch coroutine
        viewModelScope.launch {
            preferencesManager.updateSortOrderPreference(sortOrder)
        }
    }

    fun onHideCompletedChecked(hideCompleted : Boolean){
        viewModelScope.launch {
            preferencesManager.updateHideCompletedPreference(hideCompleted)
        }
    }



    fun onTaskCheckedChanged(task: Task, isChecked: Boolean){
        //Since our properties of task is immutable with val so we use copy
        viewModelScope.launch {
            taskDao.update(task.copy(completed = isChecked))
        }
    }

    fun onTaskSwipeToDelete(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
            //For proper decouple fragmant should only tell what has happened and all logic will be handled in viewModel
            //Since, as we rotate device or different layout (fragmant) will be created but the logic should be same
            //Similarly even to show any action completed event , the fragmant needs to handle and not viewModel
            //So for undoDelete action we need to give information to fragment regarding it, and fragmant will decide how to show
            //Hence, for this kind of event transmission best practice in kotlin is to use channel that helps to transfer event from one coroutine to another
            //We can use channels with flow that can suspend collection until the coroutine is active
            taskEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
            //Why not possible
//            deletedTask.value = task
            //Fragmant will be listening to this


        }

    }

    fun onUndoDeleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }


    }
    fun onTaskSelected(task: Task)= viewModelScope.launch{
        taskEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onAddNewTaskClick() = viewModelScope.launch{
        taskEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    //This will hold different kinds of events
    //Sealed class is an enum that can represent a closed combination of diff values which can contain data as actual objects
    //Events to tell fragmant to what to do
    sealed class TasksEvent{
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        object NavigateToAddTaskScreen : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()

    }


}

