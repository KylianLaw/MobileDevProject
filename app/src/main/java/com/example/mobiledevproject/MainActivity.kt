package com.example.mobiledevproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.mobiledevproject.ui.theme.MobileDevProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MobileDevProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("loggedIn", false)

        if (!isLoggedIn) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
 @Composable
 fun MainScreen(modifier: Modifier = Modifier){
     Column(
         modifier = modifier
             .fillMaxSize()
             .padding(24.dp),
         verticalArrangement = Arrangement.Center
     ){
        Text(text = "Welcome to Calorie Tracker!")
     }

 }

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    MobileDevProjectTheme {
        MainScreen()
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MobileDevProjectTheme {
        Greeting("Android")
    }

}



