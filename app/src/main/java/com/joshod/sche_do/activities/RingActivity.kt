package com.joshod.sche_do.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joshod.sche_do.R
import com.joshod.sche_do.services.AlarmService
import kotlinx.android.synthetic.main.activity_ring.*


class RingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring)
        stopAlarm.setOnClickListener {
            val intentService = Intent(
                applicationContext,
                AlarmService::class.java
            )
            applicationContext.stopService(intentService)
            finish()
        }
    }

}
