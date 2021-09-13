package com.joshod.sche_do.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshod.sche_do.R
import com.joshod.sche_do.SwipeToDeleteCallback
import com.joshod.sche_do.TaskFormDialogFragment
import com.joshod.sche_do.TasksAdapter
import com.joshod.sche_do.models.Task
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_main.*


open class MainActivity : AppCompatActivity(), TaskFormDialogFragment.Listener{

    lateinit var tasks: MutableList<Task>
    lateinit var swipeToDeleteCallback: SwipeToDeleteCallback

    open fun fetchAllTasks(): MutableList<Task>{
        return SugarRecord.listAll(Task::class.java, "time(TIME) DESC")
    }

    override fun onCreateTask(task: Task) {
        tasks.clear()
        val newTasks = fetchAllTasks()
        tasks.addAll(newTasks)
        tasksList.adapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tasks = fetchAllTasks()
        val adapter = TasksAdapter(tasks)
        tasksList.adapter = adapter
        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.addItemDecoration(DividerItemDecoration(application, DividerItemDecoration.VERTICAL))
        swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.layoutPosition
                val task = tasks[pos]
                task.unschedule(application)
                task.delete()
                tasks.removeAt(pos)
                adapter.notifyItemRemoved(pos)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(tasksList)

        newTaskButton.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val taskFormDialogFragment = TaskFormDialogFragment()
        taskFormDialogFragment.show(supportFragmentManager, TaskFormDialogFragment.TAG)
        supportFragmentManager.executePendingTransactions()
    }

}
