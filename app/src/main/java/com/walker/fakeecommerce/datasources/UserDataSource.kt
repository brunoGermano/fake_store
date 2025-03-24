package com.walker.fakeecommerce.datasources

import com.walker.fakeecommerce.model.LoginUser
import com.walker.fakeecommerce.model.Profile
import com.walker.fakeecommerce.model.User
import com.walker.fakeecommerce.network.ApiService
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val apiService: ApiService
) {

    // Adicionando os endpoints para interação com a API via links tipo: "https://api.escuelajs.co/api/v1/"

    suspend fun postUser( name: String, email: String, password: String, avatar: String ) =
        apiService.postUser( User(name, email, password, avatar ) )

    suspend fun postLogin( email: String, password: String ) =
        apiService.postLogin( LoginUser( email, password ))

    /* Aula 3.5, consumindo o getProfile do API service. */
    suspend fun getProfile() =
        apiService.getProfile()

    /* Aula 3.6, consumindo o "updateProfile" do API service. */
    suspend fun updateProfile( profile: Profile ) =
        apiService.updateProfile( profile.id, profile )

    /* Aula 3.6, consumindo o "deleteProfile" do API service. */
    suspend fun deleteProfile( id: String ) =
        apiService.deleteProfile(id)
}