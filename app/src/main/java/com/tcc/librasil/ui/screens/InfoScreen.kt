package com.tcc.librasil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.tcc.librasil.ui.theme.PinkPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(navController: NavController) {
    val jaroFont = FontFamily(Font(R.font.jaro_regular))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Informações",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinkPrimary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PinkPrimary)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
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
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = jaroFont,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Sobre o Projeto
                Text(
                    text = "Sobre o Projeto",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "O LiBRASIL é um aplicativo desenvolvido como Trabalho de Conclusão de Curso para obtenção do título de Bacharel em Ciência da Computação. O projeto tem como objetivo promover a inclusão e acessibilidade através da Língua Brasileira de Sinais (Libras), oferecendo ferramentas de tradução bidirecional entre português e Libras.",
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Justify,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Funcionalidades",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Transcrever Libras: Utilize a câmera frontal para fazer gestos em Libras e obter a tradução em texto em tempo real, utilizando inteligência artificial.\n\n" +
                            "Transcrever Português: Digite ou fale em português e visualize a tradução em Libras através do avatar 3D do VLibras.\n\n" +
                            "Biblioteca de Gestos: Consulte o alfabeto manual em Libras com imagens ilustrativas de como realizar cada letra.",
                    fontSize = 15.sp,
                    color = Color.White,
                    textAlign = TextAlign.Justify,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Card de Créditos
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Desenvolvido por:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        DeveloperCredit(
                            name = "Leonardo Melo",
                            role = "Desenvolvimento Back-End e Modelo IA"
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DeveloperCredit(
                            name = "Gabriel Gomes",
                            role = "Desenvolvimento Back-End e Modelo IA"
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DeveloperCredit(
                            name = "Paulo Henrique",
                            role = "Desenvolvimento Back-End"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Uso acadêmico e não comercial",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DeveloperCredit(name: String, role: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Text(
            text = role,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}
