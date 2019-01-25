package com.nrw.master.fromtoday

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.view.View
import android.widget.RemoteViews
import org.joda.time.DateTime

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AppWidgetConfigureActivity]
 */
class AppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            AppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        val targetDate = DateTime.now()
        val targetDayOfWeek = targetDate.getDayOfWeek() % 7/* mon = 1, sun = 7 */
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val widgetText = AppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)
            views.setTextViewText(R.id.top_value, "$targetDate.dayOfMonth")
            val res = context.resources
            for (i in 0..5) {
                val id = res.getIdentifier("week_num_$i", "id", context.packageName)
                views.apply {
                    setTextViewText(id, "${DateTime.now().weekOfWeekyear + i}")
                    setViewVisibility(id, View.VISIBLE)
                }
            }

            for (i in 0..41) {
                val id = res.getIdentifier("day_$i", "id", context.packageName)
                val day = if (i < targetDayOfWeek) targetDate.minusDays(targetDayOfWeek - i) else if (i > targetDayOfWeek) targetDate.plusDays(i - targetDayOfWeek) else targetDate

                addDayNumber(context, views, day, id)
                //addEventIntent(context, views, day, id)
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        private fun addDayNumber(context: Context, views: RemoteViews, day: DateTime, id: Int) {
            val newViews = RemoteViews(context.packageName, R.layout.day_monthly_number_view)
            newViews.setTextViewText(R.id.day_monthly_number_id, "${day.getMonthOfYear()}/${day.getDayOfMonth()}")
            Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
                setData(CalendarContract.Events.CONTENT_URI)
                putExtra(CalendarContract.Events.TITLE, "${day.millis}")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, day.millis)
                val pi = PendingIntent.getActivity(context, 0, this, PendingIntent.FLAG_ONE_SHOT)
                newViews.setOnClickPendingIntent(R.id.day_monthly_number_id, pi)
            }
            views.removeAllViews(id)
            views.addView(id, newViews)

        }
/*
        private fun addEventIntent(context: Context, views: RemoteViews, day: DateTime, id: Int) {
            Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
                setData(CalendarContract.Events.CONTENT_URI)
                putExtra(CalendarContract.Events.TITLE, "${day.millis}")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, day.millis)
                views.setOnClickFillInIntent(id, this)
            }
        }
*/
    }
}

