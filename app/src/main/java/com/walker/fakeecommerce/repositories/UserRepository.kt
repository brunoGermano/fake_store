package com.walker.fakeecommerce.repositories

import com.walker.fakeecommerce.datasources.UserDataSource
import com.walker.fakeecommerce.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDataSource: UserDataSource
) {
    suspend fun postUser( name: String, email: String, password: String, avatar: String ) =
        userDataSource.postUser( name, email, password, avatar )
}