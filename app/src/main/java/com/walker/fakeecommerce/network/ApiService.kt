package com.walker.fakeecommerce.network

import com.walker.fakeecommerce.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    /* Criando a nossa interface para os endpoints */
    @POST
    suspend fun postUser(
        @Body user: User
    ): Response<User>
}