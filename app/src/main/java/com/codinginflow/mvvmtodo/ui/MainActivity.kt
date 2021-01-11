package com.codinginflow.mvvmtodo.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.codinginflow.mvvmtodo.R
import dagger.hilt.android.AndroidEntryPoint

//Adds DI container to the class
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Using our container defined in layout for navigation
        val navHostFragmant =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        //This is  the controller that will help to navigate to fragmants inside navigation container
        navController = navHostFragmant.findNavController()

        //This will handle all setting of action bar for the fragmant with respect to fragmant layout
        setupActionBarWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        //Back handler
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
//This values is to represent if the task completed from AddEditTaskFragmant was add or edit
//We are using values other that RESULT_OK and RESULT_CANCEL
//RESULT_FIRST_USER is 1 and we can increment it for other cases ( just a convention)
const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TASK_RESULT_OK = Activity.RESULT_FIRST_USER + 1
