package com.calendarapp.simple

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calendarapp.simple.data.AppDatabase
import com.calendarapp.simple.data.TodoRepository
import com.calendarapp.simple.notification.AlarmScheduler
import com.calendarapp.simple.ui.HomeScreen
import com.calendarapp.simple.ui.TodoViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermissionIfNeeded()
        requestExactAlarmPermissionIfNeeded()
        AlarmScheduler.ensureScheduled(this)

        val database = AppDatabase.getInstance(applicationContext)
        val repository = TodoRepository(database.todoDao())

        setContent {
            MaterialTheme {
                Surface(color = Color.White) {
                    val viewModel: TodoViewModel = viewModel(
                        factory = TodoViewModel.Factory(repository)
                    )
                    HomeScreen(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 從「精準鬧鐘」設定頁返回後，補排一次鬧鐘
        AlarmScheduler.ensureScheduled(this)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}
