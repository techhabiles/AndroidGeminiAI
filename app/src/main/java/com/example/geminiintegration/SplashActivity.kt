package com.example.geminiintegration

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.geminiintegration.ui.theme.GeminiIntegrationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash Screen to show splash and permission request
 * @author TechHabiles
 */
class SplashActivity : BaseActivity() {

    @Composable
    override fun ScreenContent() {
        RenderSplash()
    }
}

@Composable
fun RenderSplash() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            context.startActivity(Intent(context, HomeActivity::class.java))
            (context as ComponentActivity).finish()
        } else {
            (context as ComponentActivity).finish()
        }
    }

    val imageModifier = Modifier
        .fillMaxWidth()
        .padding(all = 8.dp)
        .clip(shape = CircleShape)
        .border(8.dp, Color.Gray, CircleShape)
        .background(Color.Transparent)
    Column(
        modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.techhabiles),
            contentDescription = "String",
            contentScale = ContentScale.Fit,
            modifier = imageModifier
        )
        Text(text = "Gemini Compose Integration", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        // Recode Audio permission request for SpeechRecognition to work
        LaunchedEffect(key1 = "abc") {
            scope.launch {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.RECORD_AUDIO
                    )
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    delay(1200)
                    context.startActivity(Intent(context, HomeActivity::class.java))
                    (context as ComponentActivity).finish()
                } else {
                    // Request a permission
                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                }


            }
        }
    }
}

