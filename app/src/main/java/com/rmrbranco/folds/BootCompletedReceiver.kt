package com.rmrbranco.folds

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_REBOOT) {
            Log.d(TAG, "Disposition reinitialised, starting service")

            // Iniciar o serviço
            val serviceIntent = Intent(context, FoldCounterService::class.java)
            context.startService(serviceIntent)
        }
    }

    companion object {
        private const val TAG = "BootCompletedReceiver"
    }
}
