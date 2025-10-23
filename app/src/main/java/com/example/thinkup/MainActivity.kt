package com.example.thinkup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.thinkup.ui.AuthApp
import com.example.thinkup.ui.theme.ThinkUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThinkUpTheme {
                AuthApp()
            }
        }
    }
}
