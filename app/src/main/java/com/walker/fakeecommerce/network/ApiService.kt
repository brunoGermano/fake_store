package com.walker.fakeecommerce.network

import com.walker.fakeecommerce.model.AccessToken
import com.walker.fakeecommerce.model.LoginUser
import com.walker.fakeecommerce.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    /* Criando a nossa interface para os endpoints */
    @POST("users")
    suspend fun postUser(
        @Body user: User
    ): Response<User>

    @POST("auth/login") // Fazendo o post para pegar o token de acesso do usuário
    suspend fun postLogin(
        @Body loginUser: LoginUser
    ): Response<AccessToken>
}