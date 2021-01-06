package com.codinginflow.mvvmtodo.utils

import androidx.appcompat.widget.SearchView


//ViewExtension file where we can write extra code for the view that is needed


//Inline Funciton -> with inline function no new reference of lamda expression is created, rather the code is just replace where called

/*crossinline -> Due to inline funtion, there can be times that return keyword is used in lamda expression that can be misinterpreted as return of another function, as code is just copied in inline.
hence with crossinline we force the lamda funciton to not use any return statement
*/

//lamda Function - as parameter we will be recieving a lambda function that will executed in below function
//Lambda funciton is of type (String) as parameter , Unit (void) as return type , and lamda name is listener

inline fun SearchView.onQueryTextChanged(crossinline listener : (String) -> Unit){
    //here this refers the object of SearchView class that will call this funciton
    //Same as Java innerClass but with trailing lambda syntax
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            //Not doing anything here
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            //Calling our lambda everytime with newtext or Empty string (during null) when text changes
            listener(newText.orEmpty())
            return true
        }
    })
}