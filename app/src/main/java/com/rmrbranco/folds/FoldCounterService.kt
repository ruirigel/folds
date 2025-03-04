package com.rmrbranco.folds

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

class FoldCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var hinge: Sensor? = null
    private lateinit var prefs: SharedPreferences
    private var foldCount = 0
    private var lastState = false // false = aberto, true = dobrado

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        // Criar o canal de notificação
        createNotificationChannel()

        // Criar a notificação
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Fold Counter Service")
            .setContentText("Counting folds...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // Substitua com seu ícone
            .build()

        // Tornar o serviço um serviço em primeiro plano
        startForeground(1, notification)

        // Inicializar o contador a partir das preferências
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        foldCount = prefs.getInt(FOLD_COUNT_KEY, 0)

        // Inicializar o sensor de dobragem
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        // Procurar o sensor de dobragem
        for (sensor in deviceSensors) {
            if (sensor.name.contains("hinge", ignoreCase = true) ||
                sensor.name.contains("fold", ignoreCase = true)
            ) {
                hinge = sensor
                break
            }
        }

        // Registrar o listener do sensor
        hinge?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Registered bending sensor: ${it.name}")
        } ?: Log.e(TAG, "No bend sensor found!")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        return START_STICKY // Garante que o serviço seja reiniciado se for encerrado
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor == hinge) {

                Log.d(TAG, "Sensor value: ${it.values.joinToString()}") // Log dos valores
                val isFolded = it.values[0].toInt() == FOLD_THRESHOLD

                // Detectar mudança de estado (de aberto para dobrado)
                if (isFolded && !lastState) {
                    foldCount++
                    saveFoldCount()
                    updateWidget()
                    Log.d(TAG, "Fold detected! Total: $foldCount")
                }

                lastState = isFolded
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não é necessário implementar para este caso
    }

    private fun saveFoldCount() {
        prefs.edit().putInt(FOLD_COUNT_KEY, foldCount).apply()
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            FoldCounterWidgetProvider.getComponentName(this)
        )

        // Atualizar todos os widgets
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(packageName, R.layout.fold_counter_widget)
            views.setTextViewText(R.id.fold_count_text, foldCount.toString())
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Fold Counter Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Desregistrar o listener do sensor quando o serviço for destruído
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Service destroyed")

        // Parar o serviço em primeiro plano
        stopForeground(true)  // Passa true para remover a notificação
        stopSelf()  // Parar o serviço

    }

    companion object {
        private const val TAG = "FoldCounterService"
        private const val PREFS_NAME = "FoldCounterPrefs"
        private const val FOLD_COUNT_KEY = "foldCount"
        private const val FOLD_THRESHOLD = 1 // Ajustar conforme o sensor específico
        private const val CHANNEL_ID = "FoldCounterServiceChannel"
    }
}