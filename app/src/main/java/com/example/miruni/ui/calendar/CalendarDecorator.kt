package com.example.miruni.ui.calendar

import android.text.style.ForegroundColorSpan
import androidx.core.graphics.toColorInt
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

/**
 * 날짜 선택 Decorator
 */
class SelectionDecorator(private val selectedDate: CalendarDay) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == selectedDate
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan("#1AE019".toColorInt()))
    }
}

/**
 * 날짜별 Task 수에 따라 Custom Span을 적용
 */
class EventDecorator(
    private val day: CalendarDay,
    private val count: Int,
    private val countTextSize: Float
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return this.day == day
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotCountSpan(count, countTextSize))
    }
}
