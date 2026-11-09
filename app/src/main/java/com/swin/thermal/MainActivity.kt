
package com.swin.thermal

import android.app.Activity
import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ServiceInfo
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(30, 30, 30, 30)
            setBackgroundColor(Color.rgb(15, 18, 28))
        }

        val title = TextView(this).apply {
            text = "SWIN THERMAL"
            textSize = 28f
            setTextColor(Color.WHITE)
        }

        val start = Button(this).apply {
            text = "Запустить фильтр"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    val serviceIntent = Intent(
                        this@MainActivity,
                        ThermalOverlayService::class.java
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent)
                    } else {
                        startService(serviceIntent)
                    }
                } else {
                    val permissionIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(permissionIntent)
                }
            }
        }

        val stop = Button(this).apply {
            text = "Остановить фильтр"
            setOnClickListener {
                stopService(
                    Intent(this@MainActivity, ThermalOverlayService::class.java)
                )
            }
        }

        layout.addView(title)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)
    }
}

class ThermalOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var tintView: View? = null
    private var bubble: TextView? = null
    private var enabled = true

    companion object {
        private const val CHANNEL_ID = "swin_thermal"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        createNotification()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        createTint()
        createBubble()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SWIN Thermal",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            Notification.Builder(this)
        }
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentTitle("SWIN Thermal")
            .setContentText("Экранный фильтр работает")
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return START_NOT_STICKY
    }

    private fun createTint() {
        val view = View(this).apply {
            setBackgroundColor(Color.argb(35, 100, 90, 180))
        }

        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            LayoutParams.TYPE_APPLICATION_OVERLAY,
            LayoutParams.FLAG_NOT_TOUCHABLE or
                LayoutParams.FLAG_NOT_FOCUSABLE or
                LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(view, params)
        tintView = view
    }

    private fun createBubble() {
        val button = TextView(this).apply {
            text = "SWIN ON"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(55, 45, 100))
            gravity = Gravity.CENTER
            setPadding(25, 18, 25, 18)

            setOnClickListener {
                enabled = !enabled

                tintView?.visibility = if (enabled) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                text = if (enabled) "SWIN ON" else "SWIN OFF"
            }
        }

        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.TYPE_APPLICATION_OVERLAY,
            LayoutParams.FLAG_NOT_FOCUSABLE or
                LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.END
        params.x = 20
        params.y = 150

        windowManager.addView(button, params)
        bubble = button
    }

    override fun onDestroy() {
        tintView?.let {
            runCatching { windowManager.removeView(it) }
        }

        bubble?.let {
            runCatching { windowManager.removeView(it) }
        }

        tintView = null
        bubble = null

        super.onDestroy()
    }
}
