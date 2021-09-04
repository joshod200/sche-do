package com.joshod.sche_do

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joshod.sche_do.models.Task
import kotlinx.android.synthetic.main.fragment_task_form_dialog.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_task_form_dialog, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createTaskButton.setOnClickListener {
            val taskName = taskName.text.toString()
            var hour = 0
            var minute = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = taskAlarm.hour
                minute = taskAlarm.minute
            }else{
                hour = taskAlarm.currentHour
                minute = taskAlarm.currentMinute
            }
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            var time = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(calendar.time)

            if(calendar.timeInMillis < System.currentTimeMillis()) Toast.makeText(activity, "Time can't be in the past", Toast.LENGTH_SHORT).show()
            else if (taskName.isEmpty()) Toast.makeText(activity, "Name can't be blank", Toast.LENGTH_SHORT).show()
            else{
                val task = Task(taskName, time)
                task.save()
                task.schedule(context!!, calendar)
                mListener!!.onCreateTask(task)
                activity!!.supportFragmentManager.popBackStack()
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onCreateTask(task: Task)
    }

}
