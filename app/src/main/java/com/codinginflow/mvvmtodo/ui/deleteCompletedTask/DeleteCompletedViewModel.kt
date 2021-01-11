package com.codinginflow.mvvmtodo.ui.deleteCompletedTask

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteCompletedViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel(){
    //We launch deleteAllCompleted action in application scope as we want the deletion to not stop even when viewModel tends to destroy
    fun deleteAllCompletedTask() = applicationScope.launch{
        taskDao.deleteAllCompleted()
    }

}