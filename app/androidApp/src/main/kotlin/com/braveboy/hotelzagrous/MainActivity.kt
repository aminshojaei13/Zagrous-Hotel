package com.braveboy.hotelzagrous

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.braveboy.hotelzagrous.app.shared.features.admin.ReportPrinter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        ReportPrinter.init(this)

        setContent {
            App(
                isAdmin = false
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}