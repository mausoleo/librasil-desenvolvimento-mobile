package com.tcc.librasil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tcc.librasil.R
import com.tcc.librasil.navigation.Screen
import com.tcc.librasil.ui.theme.PinkPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val jaroFont = FontFamily(Font(R.font.jaro_regular))
    
    LaunchedEffect(Unit) {
        delay(2500)
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PinkPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Logo LiBRASIL
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val logoText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFFC9DAA4))) {
                        append("Li")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF8ACBB5))) {
                        append("BRASIL")
                    }
                }
                
                Text(
                    text = logoText,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = jaroFont,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Conectando pessoas através de Libras",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Texto acadêmico
            Text(
                text = "Uso acadêmico e não comercial",
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}
