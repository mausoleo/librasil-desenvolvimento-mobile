package com.tcc.librasil.data.api

import com.tcc.librasil.data.model.TranslateResponse
import com.tcc.librasil.data.model.VideoRequest
import com.tcc.librasil.data.model.VideoResponse
import com.tcc.librasil.data.model.VideoStatusResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Interface da API VLibras
 * Base URL: https://vlibras-api.gov.br/
 * 
 * Nota: A URL base pode variar. Verifique a documentação oficial.
 */
interface VLibrasApi {
    
    /**
     * Traduz texto em português para glosa em Libras
     * GET /translate?text={texto}
     */
    @GET("translate")
    suspend fun translateToGloss(
        @Query("text") text: String
    ): Response<TranslateResponse>
    
    /**
     * Solicita geração de vídeo a partir da glosa
     * POST /video
     */
    @POST("video")
    @FormUrlEncoded
    suspend fun requestVideo(
        @Field("gloss") gloss: String
    ): Response<VideoResponse>
    
    /**
     * Verifica o status da geração do vídeo
     * GET /video/status/{id}
     */
    @GET("video/status/{id}")
    suspend fun getVideoStatus(
        @Path("id") id: String
    ): Response<VideoStatusResponse>
    
    /**
     * Obtém o arquivo de vídeo gerado
     * GET /video/{id}
     */
    @GET("video/{id}")
    suspend fun getVideo(
        @Path("id") id: String
    ): Response<ByteArray>
    
    /**
     * Obtém animação para Unity Player
     * GET /{platform}/{sign}
     */
    @GET("{platform}/{sign}")
    suspend fun getAnimation(
        @Path("platform") platform: String, // ANDROID, IOS, STANDALONE, WEBGL
        @Path("sign") sign: String
    ): Response<ByteArray>
}
