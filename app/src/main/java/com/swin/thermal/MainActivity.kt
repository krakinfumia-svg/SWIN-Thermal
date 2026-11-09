package com.swin.thermal

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.view.Gravity

class MainActivity : Activity() {

    private lateinit var preview: ThermalPreview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(15, 18, 28))
        }

        val title = TextView(this).apply {
            text = "SWIN THERMAL"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val toggle = Switch(this).apply {
            text = "Тепловизионный эффект"
            setTextColor(Color.WHITE)
            isChecked = true
        }

        preview = ThermalPreview(this)

        toggle.setOnCheckedChangeListener { _, checked ->
            preview.thermalEnabled = checked
            preview.invalidate()
        }

        layout.addView(title)
        layout.addView(toggle)
        layout.addView(
            preview,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(layout)
    }
}

class ThermalPreview(context: android.content.Context) : View(context) {

    var thermalEnabled = true
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.BLACK)

        val w = width.toFloat()
        val h = height.toFloat()

        if (thermalEnabled) {
            drawHeat(canvas, w * 0.5f, h * 0.35f, w * 0.35f)
            drawHeat(canvas, w * 0.3f, h * 0.7f, w * 0.25f)
            drawHeat(canvas, w * 0.75f, h * 0.75f, w * 0.2f)
        } else {
            paint.shader = null
            paint.color = Color.DKGRAY
            canvas.drawRect(0f, 0f, w, h, paint)
        }
    }

    private fun drawHeat(
        canvas: Canvas,
        x: Float,
        y: Float,
        radius: Float
    ) {
        val colors = intArrayOf(
            Color.WHITE,
            Color.YELLOW,
            Color.RED,
            Color.BLUE,
            Color.TRANSPARENT
        )

        val positions = floatArrayOf(0f, 0.25f, 0.5f, 0.75f, 1f)

        paint.shader = RadialGradient(
            x, y, radius,
            colors, positions,
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(x, y, radius, paint)
        paint.shader = null
    }
}
