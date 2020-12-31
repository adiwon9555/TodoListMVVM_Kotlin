package com.codinginflow.mvvmtodo.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "task_table")
//Annotation to make Parcelable object which is used to pass data from one activity or fragment to another.
//Also needs to implement Parcelable class.
@Parcelize
data class Task(
    val taskName : String,
    val important : Boolean = false,
    val completed : Boolean = false,
    val created : Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0

): Parcelable {
    val createdDateFormat: String
        get() = DateFormat.getDateTimeInstance().format(created)
}