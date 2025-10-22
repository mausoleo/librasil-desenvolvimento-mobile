package com.tcc.librasil.data.repository

import com.tcc.librasil.data.api.RetrofitClient
import com.tcc.librasil.data.model.TranslateResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VLibrasRepository {
    
    private val api = RetrofitClient.vLibrasApi
    
    /**
     * Traduz texto em português para glosa em Libras
     */
    suspend fun translateToGloss(text: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.translateToGloss(text)
                if (response.isSuccessful && response.body() != null) {
                    val gloss = response.body()?.gloss ?: text
                    Result.success(gloss)
                } else {
                    Result.failure(Exception("Erro na tradução: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Solicita geração de vídeo a partir da glosa
     */
    suspend fun requestVideo(gloss: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.requestVideo(gloss)
                if (response.isSuccessful && response.body() != null) {
                    val videoId = response.body()?.id ?: ""
                    Result.success(videoId)
                } else {
                    Result.failure(Exception("Erro ao solicitar vídeo: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Verifica o status da geração do vídeo
     */
    suspend fun getVideoStatus(videoId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getVideoStatus(videoId)
                if (response.isSuccessful && response.body() != null) {
                    val status = response.body()?.status ?: "unknown"
                    Result.success(status)
                } else {
                    Result.failure(Exception("Erro ao verificar status: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
