package com.rmrbranco.folds

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rmrbranco.folds.ui.theme.FoldsTheme
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar se o app está ignorando as otimizações de bateria
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // A partir do Android Marshmallow (API 23)
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val isIgnoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(packageName)

            if (!isIgnoringBatteryOptimizations) {  // Se não estiver ignorando otimizações de bateria
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Permission required")
                builder.setMessage("To ensure the continuous operation of the app, allow background usage in the settings.\n\nAfter enabling the permission, don't forget to create the Widget.")
                builder.setPositiveButton("Go to Settings") { _, _ ->
                    // Redireciona o usuário para as configurações do aplicativo
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${packageName}")
                    startActivity(intent)
                }

                builder.setNegativeButton("Cancel") { dialog, _ ->
                    // Fecha o diálogo e encerra a atividade caso o usuário cancele
                    dialog.dismiss()
                    finish()  // Finaliza a atividade atual
                    exitProcess(0)  // Encerra o aplicativo
                }

                builder.show()  // Exibe o diálogo

            } else {

                moveTaskToBack(true)

            }
        }

        enableEdgeToEdge()
        setContent {
            FoldsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Folds",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoldsTheme {
        Greeting("Android")
    }
}
