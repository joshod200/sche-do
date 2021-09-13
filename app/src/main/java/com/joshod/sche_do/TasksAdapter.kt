package com.joshod.sche_do

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joshod.sche_do.models.Task

open class TasksAdapter(var tasks: List<Task>): RecyclerView.Adapter<TasksAdapter.TaskViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder{
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val taskItemView = inflater.inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(taskItemView)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int){
        val task: Task = tasks[position]
        val textView = holder.taskItemName
        val textView2 = holder.textView2
        textView.text = task.name
        textView2.text = task.time
    }

    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val taskItemName = itemView.findViewById<TextView>(R.id.taskItemName)!!
        val textView2 = itemView.findViewById<TextView>(R.id.textView2)!!
    }

}