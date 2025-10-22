package com.tcc.librasil.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tcc.librasil.R
import com.tcc.librasil.ml.GestureClassifier
import com.tcc.librasil.navigation.Screen
import com.tcc.librasil.ui.components.GestureLibrarySheet
import com.tcc.librasil.ui.components.CaptureHistorySheet
import com.tcc.librasil.ui.theme.GrayLight
import com.tcc.librasil.ui.theme.PinkPrimary
import com.tcc.librasil.viewmodel.GestureCaptureViewModel
import java.util.concurrent.Executors

/**
 * Tela de Captura de Gestos em Libras
 * 
 * Implementa reconhecimento em tempo real de letras em Libras usando:
 * - CameraX para captura de frames
 * - TensorFlow Lite para reconhecimento
 * 
 * Fluxo:
 * 1. Solicita permissão de câmera
 * 2. Abre câmera frontal
 * 3. Exibe preview em tela cheia
 * 4. Captura frames continuamente
 * 5. Processa com modelo TFLite
 * 6. Exibe letra reconhecida no campo "Resultado:"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestureCaptureScreen(
    navController: NavController,
    viewModel: GestureCaptureViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    
    var hasCameraPermission by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showGestureLibrary by remember { mutableStateOf(false) }
    var showCaptureHistory by remember { mutableStateOf(false) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    
    // Inicializar classifier
    val gestureClassifier = remember { GestureClassifier(context) }
    
    // Limpar recursos ao sair
    DisposableEffect(Unit) {
        onDispose {
            gestureClassifier.close()
        }
    }

    // Launcher para solicitar permissão de câmera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            showPermissionDialog = true
        }
    }

    // Solicitar permissão ao entrar na tela
    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    
    // Inicializar câmera quando permissão for concedida
    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission && previewView != null) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                try {
                    cameraProvider = cameraProviderFuture.get()
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao obter CameraProvider", e)
                    viewModel.updateError("Erro ao inicializar câmera: ${e.message}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }
    
    // Reinicializar câmera quando alternar entre frontal/traseira
    LaunchedEffect(uiState.isFrontCamera, cameraProvider, previewView) {
        if (cameraProvider != null && previewView != null) {
            try {
                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView?.surfaceProvider)
                }
                
                // Image Analysis com TFLite
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            processImageProxy(imageProxy, gestureClassifier, viewModel, uiState.isFrontCamera)
                        }
                    }
                
                // Seleciona câmera frontal ou traseira
                val cameraSelector = if (uiState.isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                
                // Desvincula casos de uso anteriores
                cameraProvider?.unbindAll()
                
                // Vincula casos de uso à câmera
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
                
                Log.d(TAG, "Câmera ${if (uiState.isFrontCamera) "frontal" else "traseira"} inicializada")
                
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao inicializar câmera", e)
                viewModel.updateError("Erro ao inicializar câmera: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.gesture_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleCamera() }) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Alternar Câmera",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { showCaptureHistory = true }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Histórico de Capturas",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { showGestureLibrary = true }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Biblioteca de Gestos",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            // Preview da câmera (ocupa maior parte da tela)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (hasCameraPermission) {
                    // Preview real da câmera
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                previewView = this
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Mensagem quando não tem permissão
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Permissão de câmera necessária",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Área de resultado (parte inferior)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PinkPrimary)
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.gesture_result_label),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = GrayLight
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.capturedText.ifEmpty { "-" },
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.capturedText.isEmpty()) 
                                    Color.Gray else Color.Black
                            )
                            
                            if (uiState.confidence > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${(uiState.confidence * 100).toInt()}% confiança",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
                
                // Exibir erro se houver
                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = Color.Yellow,
                        fontSize = 12.sp
                    )
                }
                
                // Informação
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Faça um gesto de letra em Libras para reconhecimento automático",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Biblioteca de Gestos (Bottom Sheet)
    if (showGestureLibrary) {
        GestureLibrarySheet(
            onDismiss = { showGestureLibrary = false }
        )
    }
    
    // Histórico de Capturas (Bottom Sheet)
    if (showCaptureHistory) {
        CaptureHistorySheet(
            history = uiState.captureHistory,
            onDismiss = { showCaptureHistory = false },
            onClearHistory = {
                viewModel.clearHistory()
                showCaptureHistory = false
            }
        )
    }
    
    // Dialog de permissão negada
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permissão Necessária") },
            text = { Text("A permissão de câmera é necessária para capturar gestos em Libras.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Tentar Novamente")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showPermissionDialog = false
                    navController.navigateUp()
                }) {
                    Text("Voltar")
                }
            }
        )
    }
}

/**
 * Processa um frame da câmera com TensorFlow Lite
 */
private fun processImageProxy(
    imageProxy: ImageProxy,
    classifier: GestureClassifier,
    viewModel: GestureCaptureViewModel,
    isFrontCamera: Boolean
) {
    try {
        Log.d(TAG, "Processando frame...")
        
        // Converter ImageProxy para Bitmap
        val bitmap = imageProxyToBitmap(imageProxy, isFrontCamera)
        Log.d(TAG, "Bitmap criado: ${bitmap.width}x${bitmap.height}")
        
        // Classificar com TFLite
        val result = classifier.classify(bitmap)
        
        if (result != null) {
            Log.d(TAG, "Resultado: ${result.label} (${(result.confidence * 100).toInt()}%)")
            
            // Atualizar UI se resultado válido (threshold reduzido para 30%)
            if (result.confidence > 0.3f) {
                viewModel.updateRecognitionResult(result)
            } else {
                Log.d(TAG, "Confiança muito baixa, ignorando")
            }
        } else {
            Log.e(TAG, "Resultado null do classifier")
        }
        
    } catch (e: Exception) {
        Log.e(TAG, "Erro ao processar frame", e)
        viewModel.updateError("Erro: ${e.message}")
    } finally {
        imageProxy.close()
    }
}

/**
 * Converte ImageProxy para Bitmap
 * Suporta formato YUV_420_888 (padrão do CameraX)
 */
private fun imageProxyToBitmap(imageProxy: ImageProxy, isFrontCamera: Boolean): Bitmap {
    val yBuffer = imageProxy.planes[0].buffer
    val uBuffer = imageProxy.planes[1].buffer
    val vBuffer = imageProxy.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = android.graphics.YuvImage(
        nv21,
        android.graphics.ImageFormat.NV21,
        imageProxy.width,
        imageProxy.height,
        null
    )
    
    val out = java.io.ByteArrayOutputStream()
    yuvImage.compressToJpeg(
        android.graphics.Rect(0, 0, imageProxy.width, imageProxy.height),
        100,
        out
    )
    
    val imageBytes = out.toByteArray()
    val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    
    // Rotacionar e espelhar se necessário
    val matrix = Matrix().apply {
        postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
        // Espelhar horizontalmente apenas se for câmera frontal
        if (isFrontCamera) {
            postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
    }
    
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private const val TAG = "GestureCaptureScreen"
