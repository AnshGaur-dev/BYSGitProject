package com.example.bharatyatrisathi.data

import com.example.bharatyatrisathi.API_KEY.MY_API_KEY
import com.example.bharatyatrisathi.model.CompletionRequest
import com.example.bharatyatrisathi.model.CompletionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiApi {
    @POST("generate")
    suspend fun getCompletions(@Body completionResponse: CompletionRequest) : Response<CompletionResponse>
}