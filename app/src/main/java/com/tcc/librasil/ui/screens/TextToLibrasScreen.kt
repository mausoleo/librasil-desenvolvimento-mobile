package com.tcc.librasil.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.tcc.librasil.ui.theme.PinkPrimary

/**
 * Tela de Transcrição de Português para Libras
 * 
 * Implementação usando WebView com JavaScript Bridge para controle customizado
 * do widget VLibras. Esta abordagem permite:
 * - Interface totalmente customizada
 * - Comunicação bidirecional Kotlin ↔ JavaScript
 * - Controle de estados e feedback ao usuário
 * - Esconder elementos padrão do widget
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TextToLibrasScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Estados da UI
    var inputText by remember { mutableStateOf("") }
    var translationStatus by remember { mutableStateOf<TranslationStatus>(TranslationStatus.Idle) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var shouldReloadWebView by remember { mutableStateOf(0) }
    
    // Callback para atualizar status da tradução
    val updateStatus: (TranslationStatus) -> Unit = { status ->
        translationStatus = status
        Log.d("TextToLibrasScreen", "Status atualizado: $status")
    }
    
    // Limpa TODOS os dados do WebView ao iniciar a tela
    LaunchedEffect(Unit) {
        try {
            Log.d("TextToLibrasScreen", "Iniciando limpeza profunda de dados do WebView")
            
            // Limpa diretório físico de dados do WebView
            val webViewDataDir = context.getDir("webview", android.content.Context.MODE_PRIVATE)
            webViewDataDir.deleteRecursively()
            Log.d("TextToLibrasScreen", "Diretório WebView deletado")
            
            // Limpa cache do app
            context.cacheDir.deleteRecursively()
            Log.d("TextToLibrasScreen", "Cache do app deletado")
            
            // Força recriação do WebView
            shouldReloadWebView++
            
        } catch (e: Exception) {
            Log.e("TextToLibrasScreen", "Erro ao limpar dados: ${e.message}")
        }
    }
    
    // Observa o lifecycle para recarregar WebView quando a tela volta
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Log.d("TextToLibrasScreen", "Tela retomada, forçando recriação")
                    shouldReloadWebView++
                    translationStatus = TranslationStatus.Idle
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("TextToLibrasScreen", "Tela pausada, destruindo WebView")
                    webView?.apply {
                        // Limpa tudo antes de pausar
                        clearCache(true)
                        clearHistory()
                        clearFormData()
                        destroy()
                    }
                    webView = null
                }
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            Log.d("TextToLibrasScreen", "Tela sendo destruída")
            lifecycleOwner.lifecycle.removeObserver(observer)
            webView?.destroy()
            webView = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transcrição Libras") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinkPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PinkPrimary)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de entrada de texto
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(8.dp),
                    placeholder = { Text("Digite ou fale o texto aqui...") },
                    maxLines = 6
                )
            }

            // Botão Traduzir
            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        updateStatus(TranslationStatus.Translating)
                        webView?.let { wv ->
                            // Chama função JavaScript para traduzir
                            wv.evaluateJavascript(
                                "traduzirTexto('${inputText.replace("'", "\\'")}')",
                                null
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = inputText.isNotBlank() && translationStatus !is TranslationStatus.Translating
            ) {
                Text(
                    text = "Traduzir",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Card de Status
            if (translationStatus !is TranslationStatus.Idle) {
                StatusCard(status = translationStatus)
            }

            // Área do WebView com VLibras
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Tradução em Libras:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // WebView com VLibras
                    // key = shouldReloadWebView força recriação do WebView quando muda
                    key(shouldReloadWebView) {
                        AndroidView(
                            factory = { ctx ->
                                Log.d("TextToLibrasScreen", "Criando novo WebView e limpando cache")
                                WebView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    
                                    // LIMPA TODO O CACHE E DADOS DO WEBVIEW
                                    clearCache(true)  // true = limpa arquivos de disco também
                                    clearHistory()
                                    clearFormData()
                                    
                                    // Limpa cookies
                                    android.webkit.CookieManager.getInstance().apply {
                                        removeAllCookies(null)
                                        flush()
                                    }
                                    
                                    // Limpa WebStorage (LocalStorage, SessionStorage, IndexedDB)
                                    android.webkit.WebStorage.getInstance().deleteAllData()
                                    
                                    Log.d("TextToLibrasScreen", "Cache e storage limpos completamente")
                                    
                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true  // Precisa estar true para Unity funcionar
                                        allowFileAccess = true
                                        allowContentAccess = true
                                        mediaPlaybackRequiresUserGesture = false
                                        
                                        // Desabilita cache para forçar carregamento fresco
                                        cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                                        
                                        // Habilita database mas limpa antes
                                        databaseEnabled = true  // Unity precisa disso
                                    }

                                    // Adiciona JavaScript Interface
                                    addJavascriptInterface(
                                        VLibrasJSInterface(updateStatus),
                                        "AndroidInterface"
                                    )

                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            Log.d("TextToLibrasScreen", "WebView carregado completamente (sem cache)")
                                        }
                                    }

                                    // Carrega HTML com VLibras
                                    loadDataWithBaseURL(
                                        "https://vlibras.gov.br/",
                                        getVLibrasHTML(),
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                    
                                    webView = this
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Instruções
            Text(
                text = "Digite um texto em português e clique em 'Traduzir' para ver a tradução em Libras",
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * Card de Status da Tradução
 */
@Composable
fun StatusCard(status: TranslationStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                is TranslationStatus.Translating -> Color(0xFFFFF3E0)
                is TranslationStatus.Success -> Color(0xFFE8F5E9)
                is TranslationStatus.Error -> Color(0xFFFFEBEE)
                else -> Color.White
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (status) {
                is TranslationStatus.Translating -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFFFF9800),
                        strokeWidth = 3.dp
                    )
                    Text(
                        text = "Traduzindo...",
                        fontSize = 14.sp,
                        color = Color(0xFFE65100)
                    )
                }
                is TranslationStatus.Success -> {
                    Text(
                        text = "Tradução concluída! O modelo 3D está executando a tradução.",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
                is TranslationStatus.Error -> {
                    Text(
                        text = "✗",
                        fontSize = 24.sp,
                        color = Color(0xFFF44336)
                    )
                    Text(
                        text = "Erro: ${status.message}",
                        fontSize = 14.sp,
                        color = Color(0xFFC62828)
                    )
                }
                else -> {}
            }
        }
    }
}

/**
 * Estados possíveis da tradução
 */
sealed class TranslationStatus {
    object Idle : TranslationStatus()
    object Translating : TranslationStatus()
    object Success : TranslationStatus()
    data class Error(val message: String) : TranslationStatus()
}

/**
 * Interface JavaScript para comunicação com o WebView
 */
class VLibrasJSInterface(
    private val updateStatus: (TranslationStatus) -> Unit
) {
    @JavascriptInterface
    fun onTranslationStarted() {
        Log.d("VLibrasJSInterface", "Tradução iniciada")
        updateStatus(TranslationStatus.Translating)
    }

    @JavascriptInterface
    fun onTranslationComplete() {
        Log.d("VLibrasJSInterface", "Tradução completa")
        updateStatus(TranslationStatus.Success)
    }

    @JavascriptInterface
    fun onTranslationError(error: String) {
        Log.e("VLibrasJSInterface", "Erro na tradução: $error")
        updateStatus(TranslationStatus.Error(error))
    }

    @JavascriptInterface
    fun log(message: String) {
        Log.d("VLibrasJS", message)
    }
}

/**
 * Gera o HTML com o widget VLibras customizado
 */
fun getVLibrasHTML(): String {
    return """
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VLibras</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            width: 100%;
            height: 100vh;
            overflow: hidden;
            background-color: #f5f5f5;
        }
        
        /* Posiciona o botão azul do VLibras */
        .access-button,
        .vw-access-button,
        [vw-access-button] {
            position: fixed !important;
            bottom: 10px !important;
            right: 10px !important;
            z-index: 9999 !important;
        }
        
        /* Container do player */
        #vlibras-container {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        /* Mensagem inicial */
        #initial-message {
            text-align: center;
            color: #999;
            font-family: Arial, sans-serif;
            font-size: 14px;
            padding: 20px;
        }
        
        /* Área do avatar */
        [vw] {
            width: 100% !important;
            height: 100% !important;
        }
        
        [vw-plugin-wrapper] {
            width: 100% !important;
            height: 100% !important;
        }
    </style>
</head>
<body>
    <div id="vlibras-container">
        <div id="initial-message">
            Aguardando tradução...
        </div>
    </div>

    <!-- VLibras Widget -->
    <div vw class="enabled">
        <div vw-access-button class="active"></div>
        <div vw-plugin-wrapper>
            <div class="vw-plugin-top-wrapper"></div>
        </div>
    </div>
    
    <script src="https://vlibras.gov.br/app/vlibras-plugin.js"></script>
    <script>
        // Inicializa o VLibras
        new window.VLibras.Widget('https://vlibras.gov.br/app');
        
        // Aguarda o VLibras carregar completamente
        window.addEventListener('load', function() {
            AndroidInterface.log('VLibras carregado');
            
            // Aguarda um pouco mais para garantir inicialização
            setTimeout(function() {
                AndroidInterface.log('VLibras pronto para uso');
            }, 2000);
        });
        
        /**
         * Função chamada pelo Kotlin para traduzir texto
         */
        function traduzirTexto(texto) {
            try {
                AndroidInterface.log('Traduzindo: ' + texto);
                AndroidInterface.onTranslationStarted();
                
                // Remove mensagem inicial
                document.getElementById('initial-message').style.display = 'none';
                
                // Encontra ou cria elemento de texto para o VLibras
                let textElement = document.getElementById('vlibras-text');
                if (!textElement) {
                    textElement = document.createElement('div');
                    textElement.id = 'vlibras-text';
                    textElement.style.position = 'absolute';
                    textElement.style.left = '-9999px';
                    textElement.style.opacity = '0';
                    document.body.appendChild(textElement);
                }
                
                // Define o texto
                textElement.textContent = texto;
                
                // Aguarda um momento e então ativa o VLibras
                setTimeout(function() {
                    try {
                        // Método 1: Clicar no botão azul do VLibras para abrir o widget
                        const vlibrasButton = document.querySelector('[vw-access-button]') || 
                                            document.querySelector('.access-button') ||
                                            document.querySelector('.vw-access-button');
                        
                        if (vlibrasButton) {
                            AndroidInterface.log('Botão VLibras encontrado, clicando...');
                            vlibrasButton.click();
                            
                            // Aguarda o widget abrir e então simula clique no texto
                            setTimeout(function() {
                                textElement.click();
                                AndroidInterface.log('Texto clicado para tradução');
                                
                                // Marca como completo após 3 segundos
                                setTimeout(function() {
                                    AndroidInterface.onTranslationComplete();
                                }, 3000);
                            }, 1000);
                        } else {
                            AndroidInterface.log('Botão VLibras não encontrado, tentando método alternativo');
                            
                            // Método alternativo: simular clique no texto diretamente
                            textElement.click();
                            
                            setTimeout(function() {
                                AndroidInterface.onTranslationComplete();
                            }, 2000);
                        }
                        
                    } catch (e) {
                        AndroidInterface.log('Erro ao iniciar tradução: ' + e.message);
                        AndroidInterface.onTranslationError(e.message);
                    }
                }, 500);
                
            } catch (error) {
                AndroidInterface.log('Erro: ' + error.message);
                AndroidInterface.onTranslationError(error.message);
            }
        }
        
        // Captura erros globais
        window.onerror = function(msg, url, line, col, error) {
            AndroidInterface.log('Erro JS: ' + msg);
            return false;
        };
    </script>
</body>
</html>
    """.trimIndent()
}
