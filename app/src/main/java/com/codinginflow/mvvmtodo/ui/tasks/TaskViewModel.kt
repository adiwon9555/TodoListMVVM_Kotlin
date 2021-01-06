package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

//For ViewModel we use @ViewModelInject to tell hilt how to provide an instance of Architecture component ViewModel
class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
) : ViewModel() {
    //asLivedata  converts flow to livedata, conventional practice as in view layer we use LiveData because
    //Livedata makes handling lifecycle of data easier as it is lifecycle aware
    //Meaning when fragmant goes in background and becomes inactive, livedata auto detects and stops dispatching events avoiding memory leaks and crashes
    //Flow on the other hand is used below viewModel as its flexible and has operators to work as intermediaries in converting our data as we want.
    //Also Since with Flow we can switch thread

    //MutablestateFLow is a flow that is listening for new data (HERE ->  from the UI as producer )
    //passing empty string as default init value
    val searchVal = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideComleted = MutableStateFlow(false)

    //flatMapLatest is an flow opearator that gets latest value from the flow
//    val searchedTaskFlow = searchVal.flatMapLatest {
//        taskDao.getAllTask(it)
//    }

    //Now since we have three flows, so we need to listen is any flow updates, we do this with combine
    //combine always latest values of all flows, if any changes
    //last argument of combine is a lambda where we can perform task with latest data or return it
    //since we can return only one value, so use Triple class to return three values
    //these three values are destructured and recieved in flatMapLatest to call dao and get the filtered list
    val searchedTaskFlow = combine(
        searchVal, sortOrder, hideComleted
    ) { searchVal, sortOrder, hideCompleted ->
        Triple(searchVal, sortOrder, hideCompleted)
    }.flatMapLatest {(searchVal, sortOrder, hideCompleted) ->
        taskDao.getTasks(searchVal, sortOrder, hideCompleted)
    }

    val tasks = searchedTaskFlow.asLiveData()


}

enum class SortOrder {
    BY_NAME,
    BY_DATE
}