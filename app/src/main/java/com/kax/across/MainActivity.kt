package com.kax.across

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.kax.across.ui.HomeScreen
import com.kax.across.ui.MainViewModel
import com.kax.across.ui.MainViewModelFactory
import com.kax.across.ui.theme.AcrossTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as OmniSaveApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AcrossTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
