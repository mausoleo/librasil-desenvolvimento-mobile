package com.tcc.librasil.data.model

import com.google.gson.annotations.SerializedName

/**
 * Resposta da API VLibras para tradução de texto para glosa
 */
data class TranslateResponse(
    @SerializedName("gloss")
    val gloss: String? = null,
    
    @SerializedName("text")
    val text: String? = null
)

/**
 * Request para geração de vídeo
 */
data class VideoRequest(
    @SerializedName("gloss")
    val gloss: String
)

/**
 * Resposta da API para requisição de vídeo
 */
data class VideoResponse(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("status")
    val status: String? = null
)

/**
 * Status da geração do vídeo
 */
data class VideoStatusResponse(
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("filename")
    val filename: String? = null,
    
    @SerializedName("size")
    val size: Long? = null
)
