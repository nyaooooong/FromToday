package com.nrw.master.fromtoday

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
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
        var targetDate = DateTime.now()
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val widgetText = AppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)
            views.setTextViewText(R.id.top_value, targetDate.dayOfWeek.toString())
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
                views.apply {
                   setTextViewText()
                }
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

