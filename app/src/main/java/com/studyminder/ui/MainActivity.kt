package com.studyminder.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.studyminder.R
import com.studyminder.databinding.ActivityMainBinding
import com.studyminder.service.AlarmReceiver
import com.studyminder.service.NotificationService
import com.studyminder.ui.dashboard.DashboardFragment
import com.studyminder.ui.schedule.ScheduleFragment
import com.studyminder.ui.subjects.SubjectsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AlarmReceiver.createNotificationChannel(this)
        requestPermissions()
        startNotificationService()

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> loadFragment(DashboardFragment())
                R.id.nav_subjects -> loadFragment(SubjectsFragment())
                R.id.nav_schedule -> loadFragment(ScheduleFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun startNotificationService() {
        val intent = Intent(this, NotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun requestPermissions() {
        val permsNeeded = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) permsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permsNeeded.toTypedArray(), 100)
        }
    }
}
