package com.walker.fakeecommerce

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.walker.fakeecommerce.datasources.UserDataSource
import com.walker.fakeecommerce.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/* Cria a árvore de dependências do nosso app*/

@Module
@InstallIn(SingletonComponent::class)
class ModuleDI {
    /* Provendo uma instância do firebase para o nosso app*/
    @Provides
    @Singleton
    fun providesStorageReference(): StorageReference =
        FirebaseStorage.getInstance().reference


    @Provides
    fun providesBaseUrl() = "https://api.escuelajs.co/api/v1"

    @Provides
    fun provideRetrofit(
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    @Provides
    @Singleton
    fun provideApiService( retrofit: Retrofit ): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesUserDataSource( apiService: ApiService ) =
        UserDataSource( apiService )
}

