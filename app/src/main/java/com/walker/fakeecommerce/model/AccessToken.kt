package com.walker.fakeecommerce.model

import com.google.gson.annotations.SerializedName

data class AccessToken (
    @SerializedName("access_token") // essa notação do kapt, "@SerializedName", retorna o valor "access_token" para o atributo "accessToken" criado abaixo
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)