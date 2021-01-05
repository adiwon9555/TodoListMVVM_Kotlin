package com.codinginflow.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.codinginflow.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


//This module is actually a dependency container for application level and it provides or injects dependency where needed.
@Module
@InstallIn(ApplicationComponent::class)
//With InstallIn we make this container app level

//Creating Object instead class means only one instance will be there.
//Singleton as it can not be used in right hand of expression
object AppModule {

    //inject does the same as Provides but here we write as Provides because we are using external classes and libraries to create our dependency.
    //eg. we use Room.databaseBuilder to create the object
    @Provides
    @Singleton
    fun provideDatabase(
        app : Application,
        callback: TaskDatabase.Callback
    ) = Room.databaseBuilder(app,TaskDatabase::class.java,"task_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()
    // with equals it is kotlin shorthand for direct return

    @Provides
    fun provideTaskDao(db : TaskDatabase) = db.getTaskDao()

    @ApplicationScope
    @Provides
    @Singleton
    //Creating a application scoped coroutine for background tasks
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

//Creating a custom annotation to differentiate our application scoped coroutine with another
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope