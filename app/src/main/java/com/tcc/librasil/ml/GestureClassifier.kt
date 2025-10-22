package com.tcc.librasil.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Classificador de Gestos em Libras usando TensorFlow Lite
 * 
 * Processa frames da câmera e reconhece letras em Libras.
 * 
 * Modelo: libras_model.tflite
 * - Input: [1, 224, 224, 3] (imagem 224x224 RGB)
 * - Output: [1, 16] (16 classes/letras)
 */
class GestureClassifier(context: Context) {
    
    private var interpreter: Interpreter? = null
    private val inputImageWidth = 224
    private val inputImageHeight = 224
    private val pixelSize = 3 // RGB
    
    // Labels das 16 letras reconhecidas pelo modelo
    // Ajuste conforme seu modelo específico
    private val labels = listOf(
        "A", "B", "C", "D", "E", "F", 
        "G", "H", "I", "L", "M", "N", 
        "O", "P", "U", "V"
    )
    
    init {
        try {
            // Carregar modelo do assets
            val modelBuffer = FileUtil.loadMappedFile(context, "libras_model.tflite")
            
            // Configurar opções do interpretador
            val options = Interpreter.Options().apply {
                setNumThreads(4) // Usar 4 threads para melhor performance
            }
            
            // Criar interpretador
            interpreter = Interpreter(modelBuffer, options)
            
            Log.d(TAG, "Modelo TFLite carregado com sucesso")
            Log.d(TAG, "Input shape: [1, $inputImageHeight, $inputImageWidth, $pixelSize]")
            Log.d(TAG, "Output shape: [1, ${labels.size}]")
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar modelo TFLite", e)
        }
    }
    
    /**
     * Classifica um frame da câmera e retorna a letra reconhecida
     * 
     * @param bitmap Frame capturado da câmera
     * @return Letra reconhecida (A-Z) ou null se erro
     */
    fun classify(bitmap: Bitmap): ClassificationResult? {
        try {
            if (interpreter == null) {
                Log.e(TAG, "Interpretador não inicializado")
                return null
            }
            
            // 1. Preprocessar imagem
            val resizedBitmap = Bitmap.createScaledBitmap(
                bitmap,
                inputImageWidth,
                inputImageHeight,
                true
            )
            
            // 2. Converter para ByteBuffer
            val inputBuffer = convertBitmapToByteBuffer(resizedBitmap)
            
            // 3. Preparar output
            val outputArray = Array(1) { FloatArray(labels.size) }
            
            // 4. Executar inferência
            interpreter?.run(inputBuffer, outputArray)
            
            // 5. Processar resultado
            val probabilities = outputArray[0]
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
            val confidence = probabilities[maxIndex]
            val label = labels[maxIndex]
            
            Log.d(TAG, "Reconhecido: $label (confiança: ${(confidence * 100).toInt()}%)")
            
            return ClassificationResult(
                label = label,
                confidence = confidence
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao classificar imagem", e)
            return null
        }
    }
    
    /**
     * Converte Bitmap para ByteBuffer normalizado
     * 
     * Normaliza pixels para range [0, 1] (float32)
     */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(
            4 * inputImageWidth * inputImageHeight * pixelSize
        )
        byteBuffer.order(ByteOrder.nativeOrder())
        
        val intValues = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(
            intValues, 0, bitmap.width,
            0, 0, bitmap.width, bitmap.height
        )
        
        var pixel = 0
        for (i in 0 until inputImageHeight) {
            for (j in 0 until inputImageWidth) {
                val value = intValues[pixel++]
                
                // Extrair RGB e normalizar para [0, 1]
                byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f) // R
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)  // G
                byteBuffer.putFloat((value and 0xFF) / 255.0f)          // B
            }
        }
        
        return byteBuffer
    }
    
    /**
     * Libera recursos do interpretador
     */
    fun close() {
        interpreter?.close()
        interpreter = null
        Log.d(TAG, "Interpretador TFLite fechado")
    }
    
    companion object {
        private const val TAG = "GestureClassifier"
    }
}

/**
 * Resultado da classificação
 * 
 * @property label Letra reconhecida (A-Z)
 * @property confidence Confiança da predição (0.0 a 1.0)
 */
data class ClassificationResult(
    val label: String,
    val confidence: Float
)
