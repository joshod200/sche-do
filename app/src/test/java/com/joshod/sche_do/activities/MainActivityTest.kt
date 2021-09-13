package com.joshod.sche_do.activities

import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import com.joshod.sche_do.R
import com.joshod.sche_do.models.Task
import com.orm.SugarContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.android.controller.ComponentController

import android.app.Activity
import androidx.recyclerview.widget.ItemTouchHelper
import com.joshod.sche_do.TaskFormDialogFragment
import com.orm.SugarApp
import com.orm.SugarDb
import junit.framework.TestCase.assertNotNull
import kotlinx.android.synthetic.main.activity_main.*
import org.mockito.Mockito.*
import org.robolectric.RuntimeEnvironment

import org.robolectric.android.controller.ActivityController
import java.lang.reflect.Field


@Config(sdk = [Build.VERSION_CODES.N])
@RunWith(RobolectricTestRunner::class)
class MainActivityTest{

    lateinit var mainActivity: MainActivity

    fun buildTask(): Task {
        return mock(Task::class.java)
    }

    fun mockSugarRecordFetchAll(): MutableList<Task>{
        val newTasks = mutableListOf<Task>()
        for (i in 0..12) {
            newTasks.add(buildTask())
        }
        return newTasks
    }

    fun mockSugarRecordSave(task: Task): MutableList<Task>{
        val allTasks = mockSugarRecordFetchAll()
        allTasks.add(task)
        return allTasks
    }

    @Before
    fun setUp() {
        val ac =  Robolectric.buildActivity(MainActivity::class.java)
        mainActivity = spy(ac.get())
        doReturn(mockSugarRecordFetchAll()).`when`(mainActivity).fetchAllTasks()
        replaceComponentInActivityController(ac, mainActivity)
        ac.create().resume().visible().get()
    }

    @Test
    fun rvShouldBePopulatedWithTasks() {
        verify(mainActivity, times(1)).fetchAllTasks()
        assertEquals(mainActivity.tasks.size, 13)
    }

    @Test
    fun taskIsAddedToTasksList(){
        val task = buildTask()
        doReturn(mockSugarRecordSave(task)).`when`(mainActivity).fetchAllTasks() // return updated list after 'save'
        mainActivity.onCreateTask(task)
        verify(mainActivity, times(2)).fetchAllTasks()
        assert(mainActivity.tasks.contains(task))
    }

    @Test
    fun swipesToDelete(){
        val rv = mainActivity.findViewById<RecyclerView>(R.id.tasksList)
        val position = 0
        rv.scrollToPosition(position)
        val viewHolder = rv.findViewHolderForAdapterPosition(position)
        val tasks = mainActivity.tasks
        val task = tasks[position]
        mainActivity.swipeToDeleteCallback.onSwiped(viewHolder!!, ItemTouchHelper.LEFT)
        verify(task, times(1)).unschedule(any())
        verify(task, times(1)).delete()
        assert(!tasks.contains(task))
    }

    @Test
    fun opensTasksFormDialog(){
        val newTaskButton = mainActivity.newTaskButton
        newTaskButton.performClick()
        val dialog = mainActivity.supportFragmentManager.findFragmentByTag(TaskFormDialogFragment.TAG)
        assertNotNull(dialog)
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun replaceComponentInActivityController(
        activityController: ActivityController<*>?,
        activity: Activity?
    ) {
        val componentField: Field = ComponentController::class.java.getDeclaredField("component")
        componentField.isAccessible = true
        componentField.set(activityController, activity)
    }

}