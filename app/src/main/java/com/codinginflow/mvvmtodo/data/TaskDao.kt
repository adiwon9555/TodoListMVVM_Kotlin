package com.codinginflow.mvvmtodo.data

import androidx.room.*
import com.codinginflow.mvvmtodo.ui.tasks.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    //This funtion is called by viewmodel which decides with what to sort
    //We do the sorting in different function as in sql, column name can not be replace with passed parameter
    fun getTasks(
        searchText: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_NAME ->
                getTasksOrderByName(searchText, hideCompleted)
            SortOrder.BY_DATE ->
                getTasksOrdeByDateCreated(searchText, hideCompleted)
        }


    // || is append
    //logic for completed is to show incomplete once always and hide completed once when wanted
    @Query("SELECT * FROM task_table where (completed != :hideCompleted OR completed=0) AND taskName LIKE '%' || :searchText || '%' ORDER BY important DESC, taskName")
    //Flow is built on top of coroutine hence, suspend for this function is not needed.
    fun getTasksOrderByName(searchText: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table where (completed != :hideCompleted OR completed=0) AND taskName LIKE '%' || :searchText || '%' ORDER BY important DESC, created")
    fun getTasksOrdeByDateCreated(searchText: String, hideCompleted: Boolean): Flow<List<Task>>


    //Since db operation can take time to complete hence we call it with suspend function which will run in a coroutine
    //So that these are run in different thread and Main UI is not blocked.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

}