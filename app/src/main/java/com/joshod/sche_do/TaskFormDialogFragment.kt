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

    companion object{
        const val TAG = "TASK_FORM"
    }

    private var mListener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_form_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createTaskButton.setOnClickListener {
            val taskName = taskName.text.toString()
            val hour: Int
            val minute: Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = taskAlarm.hour
                minute = taskAlarm.minute
            }else{
                hour = taskAlarm.currentHour
                minute = taskAlarm.currentMinute
            }
            val calendar = Task.makeCalendar(hour, minute)
            val time = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(calendar.time)

            when {
                calendar.timeInMillis < System.currentTimeMillis() -> Toast.makeText(activity, "Time can't be in the past", Toast.LENGTH_SHORT).show()
                taskName.isEmpty() -> Toast.makeText(activity, "Name can't be blank", Toast.LENGTH_SHORT).show()
                else -> {
                    val task = Task(taskName, time)
                    task.save()
                    task.schedule(requireContext(), calendar)
                    mListener!!.onCreateTask(task)
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as Listener
        } else {
            context as Listener
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
