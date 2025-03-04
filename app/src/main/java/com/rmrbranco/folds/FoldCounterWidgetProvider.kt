package com.rmrbranco.folds

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.util.Log

/**
 * [FoldCounterWidgetProvider] is responsible for managing the Fold Counter widget.
 *
 * This class handles widget updates, including retrieving the current fold count
 * from shared preferences, updating the widget's UI with the count, and ensuring
 * the [FoldCounterService] is running.
 */

class FoldCounterWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        Log.d(TAG, "Widget actualization")

        // Atualizar os widgets com a contagem atual
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val foldCount = prefs.getInt(FOLD_COUNT_KEY, 0)

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, foldCount)
        }

        // Iniciar o serviço se ainda não estiver em execução
        val serviceIntent = Intent(context, FoldCounterService::class.java)
        context.startService(serviceIntent)
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        foldCount: Int,
    ) {
        val views = RemoteViews(context.packageName, R.layout.fold_counter_widget)
        views.setTextViewText(R.id.fold_count_text, foldCount.toString())
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        private const val TAG = "FoldCounterWidget"
        private const val PREFS_NAME = "FoldCounterPrefs"
        private const val FOLD_COUNT_KEY = "foldCount"

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, FoldCounterWidgetProvider::class.java)
        }
    }
}