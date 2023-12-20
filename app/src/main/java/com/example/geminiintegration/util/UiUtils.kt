package com.example.geminiintegration.util


import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity

/**
 * Utility class to UI
 * @author TechHabiles
 */
class UiUtils {
    companion object{
        fun showScreen(context: Context, activity: Class<ComponentActivity> ){
            context.startActivity(Intent(context, activity))
        }
    }
}