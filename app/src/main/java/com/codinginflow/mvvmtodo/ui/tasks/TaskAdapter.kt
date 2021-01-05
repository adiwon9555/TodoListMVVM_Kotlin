package com.codinginflow.mvvmtodo.ui.tasks

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.ActivityMainBinding
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding

//Adapter connects our data objects holding data with the view(here recyclerView)
class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {
    private val TAG = "TaskAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        //ItemTaskBinding is a special that is created compile time from item_task.xml(any layout file), that helps in binding.
        //with binding now we dont need to call findViewById for any layout element, avoiding exceptions that can not be caught in compile time.
        //Here layout inflating means converting our xml layouts to objects to be used in code
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

        val currentItem = getItem(position)
        //Each holder or row of the recycler view is filled here
        holder.bind(currentItem)
    }



    class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val TAG = "TaskAdapter"
        fun bind(task: Task) {
            //kotlin shorthand syntax to avoid repeated use of an object to call its properties
            binding.apply {
                taskName.text = task.taskName;
                checkboxComplete.isChecked = task.completed;
                taskName.paint.isStrikeThruText = task.completed
                priorityIcon.isVisible = task.important
            }
        }

    }
    //ListAdapter needs this DiffCallback class to check if any items or item position has changed for proper animation.
    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            //if the position has changed then this will return false
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            //since our Task is data class hence with == we automatically compare each property of task
            oldItem == newItem

    }
}