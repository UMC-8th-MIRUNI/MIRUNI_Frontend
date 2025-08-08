package com.example.miruni.ui.calendar

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import androidx.core.graphics.toColorInt

/**
 * 날짜별 일정 갯수 표시하는 Span
 */
class DotCountSpan(
    private val count: Int,
    private val countTextSize: Float
): LineBackgroundSpan {

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lineNumber: Int
    ) {
        if (count <= 0) return

        val originalColor = paint.color
        val originalTextSize = paint.textSize
        val originalAlign = paint.textAlign

        val centerX = (left + right) / 2f
        val centerY = baseline + 24f
        val radius = 20f

        paint.color = getColorForCount(count)
        canvas.drawCircle(centerX, centerY, radius, paint)

        paint.color = Color.WHITE
        paint.textSize = countTextSize
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("$count", centerX, centerY + countTextSize / 3, paint)

        paint.color = originalColor
        paint.textSize = originalTextSize
        paint.textAlign = originalAlign
    }

    private fun getColorForCount(count: Int): Int {
        return when (count) {
            in 1..5 -> "#A7CAFB".toColorInt()
            in 6..10 -> "#FFAA00".toColorInt()
            in 11..15 -> "#FF5500".toColorInt()
            else -> "#FF0000".toColorInt()
        }
    }
}
