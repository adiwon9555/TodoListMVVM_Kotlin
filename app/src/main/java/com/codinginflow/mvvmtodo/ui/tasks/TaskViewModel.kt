package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.codinginflow.mvvmtodo.data.TaskDao
import javax.inject.Inject

//For ViewModel we use @ViewModelInject to tell hilt how to provide an instance of Architecture component ViewModel
class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao
): ViewModel() {
    //asLivedata  converts flow to livedata, conventional practice as in view layer we use LiveData because
    //Livedata makes handling lifecycle of data easier as it is lifecycle aware
    //Meaning when fragmant goes in background and becomes inactive, livedata auto detects and stops dispatching events avoiding memory leaks and crashes
    //Flow on the other hand is used below viewModel as its flexible and has operators to work as intermediaries in converting our data as we want.
    //Also Since with Flow we can switch thread
    val tasks = taskDao.getAllTask().asLiveData()


}