package com.example.geminiintegration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.geminiintegration.ui.theme.GeminiIntegrationTheme

/**
 *  Base Activity to show loading indicator and content
 *  @author TechHabiles
 */
abstract class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GeminiIntegrationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {

                    IndeterminateCircularIndicator(getDataViewModel())
                }
            }
        }
    }

    open fun getDataViewModel(): BaseViewModel {
        return ViewModelProvider(this).get(BaseViewModel::class.java)
    }

    @Composable
    abstract fun ScreenContent()

    @Composable
    fun IndeterminateCircularIndicator(viewModel: BaseViewModel = BaseViewModel()) {
        ScreenContent()
        val isLoading by viewModel.isLoading.collectAsState()

        if (!isLoading) return
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp),
                color = MaterialTheme.colorScheme.background,
                trackColor = Color.Red,
                strokeWidth = 8.dp
            )
        }

    }

}

