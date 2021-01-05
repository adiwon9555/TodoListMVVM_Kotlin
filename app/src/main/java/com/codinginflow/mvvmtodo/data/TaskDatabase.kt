package com.codinginflow.mvvmtodo.data

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun getTaskDao() : TaskDao

    //These callbacks are used any time of database creation or open acc. to function we override.
    class Callback @Inject constructor(
        //since we have already injected callback to provideTaskDatabase, so injecting the same here will create circular dependency.
        //To avoid that we use Provider<TaskDatabase> we helps us get dependency lazily, meaning only on calling get() on Provider we will actually get the dependency
        //So only after super.onCreate(db) method we will call get, avoiding circular dependency.
//        private val dataBase: Provider<TaskDatabase>
        //Or directly
        private val dao : Provider<TaskDao>,
        @ApplicationScope private val applicationScope : CoroutineScope
    ) : RoomDatabase.Callback(){
        private  val TAG = "TaskDatabase"

        //we want some data as soon as db is created hence we use onCreate function as callback
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
//            val dao = dataBase.get().getTaskDao();
            //Or directly use the injected dao
            Log.d(TAG, "onCreate: callback")
            val taskDao = dao.get();
            applicationScope.launch {
                Log.d(TAG, "onCreate: callback in launch")
                taskDao.insert(Task("Finish This Tutorial",important = true))
                taskDao.insert(Task("Wash the dishes"))
                taskDao.insert(Task("Do the laundry"))
                taskDao.insert(Task("Buy groceries", important = true))
                taskDao.insert(Task("Prepare food", completed = true))
                taskDao.insert(Task("Call mom"))
                taskDao.insert(Task("Visit grandma", completed = true))
                taskDao.insert(Task("Repair my bike"))
                taskDao.insert(Task("Call Elon Musk"))
            }


        }

    }


}