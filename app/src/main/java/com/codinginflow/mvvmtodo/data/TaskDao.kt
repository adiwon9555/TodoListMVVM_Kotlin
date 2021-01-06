package com.codinginflow.mvvmtodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // || is append
    @Query("SELECT * FROM task_table where taskName LIKE '%' || :searchText || '%' ORDER BY important DESC")
    //Flow is built on top of coroutine hence, suspend for this function is not needed.
    fun getAllTask(searchText : String) : Flow<List<Task>>


    //Since db operation can take time to complete hence we call it with suspend function which will run in a coroutine
    //So that these are run in different thread and Main UI is not blocked.
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

}