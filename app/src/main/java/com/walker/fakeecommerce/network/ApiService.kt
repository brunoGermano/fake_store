package com.walker.fakeecommerce.network

import com.walker.fakeecommerce.model.AccessToken
import com.walker.fakeecommerce.model.LoginUser
import com.walker.fakeecommerce.model.Product
import com.walker.fakeecommerce.model.Profile
import com.walker.fakeecommerce.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    /* TODAS AS FUNÇÕES AQUI DEVEM SER SUSPENSAS, já que estão consumindo de uma API service, ou seja,
       um serviço da internet. Assim sendo, precisamos esperar pelo retorno da internet, logo, executamos
       em uma Thread fora da "MainThread". Isso faz com que os dados não fiquem vazios pelo fato da
       "MainThread"  terminar seu processamento antes do retorno da API. No entanto, quando a funções
       supensas em Threads fora da "MainThread" aqui terminarem seu processamento, elas devolvem esse
       valor para "MainThread", e esta, por sua vez, termina seu processamento. */


    /* Criando o método de end point GET */
    /* Colocamos a paginação pois são retornados 200 itens da API e não carrega isso, pois o "LazyColumn"
       não consegue lidar com tantos itens na lista sem comprometer a performance dele. Colocamos o
       parâmetro "?offset=0&limit=10" na "url" a ser gerada pelo "Kapt", isso trará apenas 10 itens
       começando do primeiro que é o da posição zero. */
    @GET("products?offset=0&limit=10")
    suspend fun getProducts(): Response<List<Product>> // End point que trará como resposta uma lista de produtos


    /*Aula 3.5
      Adicionando o endpoint para adquirir o profile do usuário. */
    @GET("auth/profile")
    suspend fun getProfile(): Response<Profile> // End point que trará como resposta uma lista de produtos



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