package com.rmrbranco.folds

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * BootCompletedReceiver is a BroadcastReceiver that listens for system boot completion and reboot events.
 *
 * Upon receiving either of these broadcasts, it starts the FoldCounterService. This ensures that the
 * FoldCounterService is running after the device has been rebooted or has completed the initial boot process.
 */

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_REBOOT) {
            Log.d(TAG, "Disposition reinitialised, starting service")


            val serviceIntent = Intent(context, FoldCounterService::class.java)
            context.startService(serviceIntent)
        }
    }

    companion object {
        private const val TAG = "BootCompletedReceiver"
    }
}
