package com.example.miruni

import android.content.Context
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

//class eventDecorator(private val context: Context, scheduleList: List<Schedule>): DayViewDecorator {
//class eventDecorator(private val context: Context, scheduleList: List<String>): DayViewDecorator {
//
//    private val eventDates = HashSet<CalendarDay>()
//
//    init {
//        scheduleList.forEach { schedule ->
//            val date = CalendarDay.from(schedule.date[0], schedule.date[1], schedule.date[2])
//            eventDates.add(date)
//        }
//    }
//
//    override fun shouldDecorate(day: CalendarDay?): Boolean {
//        return eventDates.contains(day)
//    }
//
//    override fun decorate(view: DayViewFacade?) {
//        view?.addSpan(DotSpan(10F, ContextCompat.getColor(context, R.color.isSubmitted)))
//    }
//}

class selectDecorator(private val context: Context, private var date: CalendarDay?) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day == date
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(
            ContextCompat.getDrawable(context, R.drawable.selector_circle)!!
        )
    }

    fun setDate(newDate: CalendarDay?) {
        this.date = newDate
    }
}
