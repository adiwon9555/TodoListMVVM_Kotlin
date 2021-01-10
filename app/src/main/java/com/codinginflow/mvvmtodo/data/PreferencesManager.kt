package com.codinginflow.mvvmtodo.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

//This class is to basically a manager for Jetpack datastore to hold preferences of our app
//previously SharedPreferences were used for this, but since it had some flaws like working in UI thread, it is to be deprecated
//We now use jetpack datastore that uses flow and coroutine to work in background

private const val TAG = "PreferencesManager"
data class FilteredPreference(val sortOrder: SortOrder,val hideCompleted : Boolean)

enum class SortOrder {
    BY_NAME,
    BY_DATE
}

class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
){
    //creating application scoped datastore
    private val datastore = context.createDataStore("user_preferences")
    //data return flow of preferences with key value
    //Hence its better to have it in our own data class (FilteredPreference) for easy use, which can be done using flow operators like map
    //Also it is best to catch any exception and handle it
    val preferencesFLow = datastore.data
        .catch {    exception ->
            if (exception is IOException){
                Log.e(TAG, "Error reading preferences: ",exception )
                //We know that its a reading error so, we just emit empty preferences to map which will handle and return default values as defined
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map {  preferences ->
            //?: if null or empty then take the right operand
            //For sortorder storing enum
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name)
            val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
            //map is an inline function with crossInline hence no ruturn needed
            FilteredPreference(sortOrder,hideCompleted)
        }

    //functions to update our prefences, we use suspend to do in Background
    //For reading already Flow is in background, whereas for editing we need to run in coroutine ourselves, hence suspend
    suspend fun updateSortOrderPreference(sortOrder: SortOrder){
        datastore.edit {
            it[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompletedPreference(hideCompleted: Boolean){
        datastore.edit {
            it[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    //Our custom object for easy getting of preferences key
    private object PreferencesKeys{
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}