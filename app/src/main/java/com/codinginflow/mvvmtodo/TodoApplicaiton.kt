package com.codinginflow.mvvmtodo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//The setup needed for daggerHilt to work, and kick starting it.
@HiltAndroidApp

//This class now needs to be set inside the manifest Application tag to make it the Application class of the app.
//Allowing hilt to do dependency injection in the app
class TodoApplicaiton : Application() {
}