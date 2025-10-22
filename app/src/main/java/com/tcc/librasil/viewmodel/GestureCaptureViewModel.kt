package com.tcc.librasil.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tcc.librasil.ml.ClassificationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GestureCaptureUiState(
    val isCapturing: Boolean = false,
    val capturedText: String = "",
    val confidence: Float = 0f,
    val errorMessage: String? = null,
    val captureHistory: List<String> = emptyList(),
    val isFrontCamera: Boolean = true
)

class GestureCaptureViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(GestureCaptureUiState())
    val uiState: StateFlow<GestureCaptureUiState> = _uiState.asStateFlow()
    
    private var lastUpdateTime = 0L
    private val updateDelayMs = 1500L // 1.5 segundos
    
    /**
     * Atualiza o resultado do reconhecimento com delay de 1.5s
     * Chamado pelo ImageAnalysis quando um gesto é reconhecido
     */
    fun updateRecognitionResult(result: ClassificationResult) {
        val currentTime = System.currentTimeMillis()
        
        // Só atualiza se passou o tempo de delay
        if (currentTime - lastUpdateTime >= updateDelayMs) {
            viewModelScope.launch {
                val newHistory = _uiState.value.captureHistory.toMutableList()
                newHistory.add(result.label)
                
                _uiState.value = _uiState.value.copy(
                    capturedText = result.label,
                    confidence = result.confidence,
                    errorMessage = null,
                    captureHistory = newHistory
                )
                
                lastUpdateTime = currentTime
            }
        }
    }
    
    /**
     * Atualiza mensagem de erro
     */
    fun updateError(message: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                errorMessage = message
            )
        }
    }
    
    /**
     * Limpa o texto capturado
     */
    fun clearCapturedText() {
        _uiState.value = _uiState.value.copy(
            capturedText = "",
            confidence = 0f,
            errorMessage = null
        )
    }
    
    /**
     * Alterna entre câmera frontal e traseira
     */
    fun toggleCamera() {
        _uiState.value = _uiState.value.copy(
            isFrontCamera = !_uiState.value.isFrontCamera
        )
    }
    
    /**
     * Limpa o histórico de capturas
     */
    fun clearHistory() {
        _uiState.value = _uiState.value.copy(
            captureHistory = emptyList()
        )
    }
}
