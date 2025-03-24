package com.walker.fakeecommerce.network

import com.walker.fakeecommerce.model.AccessToken
import com.walker.fakeecommerce.model.LoginUser
import com.walker.fakeecommerce.model.Product
import com.walker.fakeecommerce.model.Profile
import com.walker.fakeecommerce.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    /* Criando a nossa interface para os endpoints */

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
    @GET("auth/profile") /* esse endpoint precisa receber o token do usuário logado para devolver os dados dele. */
    suspend fun getProfile(): Response<Profile> // End point que trará como resposta uma lista de produtos



    /* Aula 3.6, Criando o novo endpoint para edição do perfil com caminho dinâmico.
       O verbo HTTP "PUT" é usado para editar algo na API. */
    /* O valor de id será dinâmico, isso me dará o id do profile que eu estiver logado no momento
       para enviar para a API e alterar o perfil do usuário logado em questão. */
    @PUT("users/{id}")
    suspend fun updateProfile(
        @Path("id") id: String,
        @Body profile: Profile
    ): Response<Profile>  /* Como resposta da API temos o perfil modificado pelo usuário, para ver ela use o "appInspection" aba response com o json retornado. */


    /* Aula 3.6, Criando o novo endpoint para deleção do perfil com caminho dinâmico. */
    @DELETE("users/{id}")
    suspend fun deleteProfile(
        @Path("id") id: String
    ): Response<Boolean> /* O retorno da API depois de executar a deleção do perfil é "true" ou "false", por isso
    usa-se o Boolean no generics da classe de retorno "Response" pois será o tipo que ela retornará. */



    /* Endpoint para criar um usuário. */
    @POST("users")
    suspend fun postUser(
        @Body user: User
    ): Response<User>

    @POST("auth/login") // Fazendo o post para pegar o token de acesso do usuário
    suspend fun postLogin(
        @Body loginUser: LoginUser
    ): Response<AccessToken>

}