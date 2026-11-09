package com.swin.thermal

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
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
            text = "SWIN THERMAL T7"
            textSize = 26f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val info = TextView(this).apply {
            text = "Чёрно-белый экранный фильтр"
            textSize = 16f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        val start = Button(this).apply {
            text = "ЗАПУСТИТЬ"
        }

        val stop = Button(this).apply {
            text = "ОСТАНОВИТЬ"
        }

        layout.addView(title)
        layout.addView(info)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)
    }
}
