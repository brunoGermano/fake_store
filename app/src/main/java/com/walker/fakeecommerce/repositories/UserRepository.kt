package com.walker.fakeecommerce.repositories

import com.walker.fakeecommerce.datasources.UserDataSource
import com.walker.fakeecommerce.model.LoginUser
import com.walker.fakeecommerce.model.Profile
import com.walker.fakeecommerce.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDataSource: UserDataSource
) {
    suspend fun postUser( name: String, email: String, password: String, avatar: String ) =
        userDataSource.postUser( name, email, password, avatar )

    suspend fun postLogin( email: String, password: String ) =
        userDataSource.postLogin( email, password )

    /* Aula 3.5, consumindo o "getProfile" do "UserDataSource". */
    suspend fun getProfile() =
        userDataSource.getProfile()

    /* Aula 3.6, consumindo o "updateProfile" do "UserDataSource". */
    suspend fun updateProfile( profile: Profile) =
        userDataSource.updateProfile( profile )

    /* Aula 3.6, consumindo o "deleteProfile" do "UserDataSource". */
    suspend fun deleteProfile( id: String ) =
        userDataSource.deleteProfile(id)
}