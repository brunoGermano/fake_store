package com.walker.fakeecommerce

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.walker.fakeecommerce.datasources.ProductsDataSource
import com.walker.fakeecommerce.datasources.UserDataSource
import com.walker.fakeecommerce.network.ApiService
import com.walker.fakeecommerce.repositories.ProductsRepository
import com.walker.fakeecommerce.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/* Cria a árvore de dependências do nosso app*/

@Module
@InstallIn(SingletonComponent::class)
class ModuleDI {

    /* Provendo instância do sharedPreferences pra toda a nossa aplicação */
    @Provides
    @Singleton
    fun provideSharedPreference(
        /* Precisamos de um contexto aqui dentro, para isso usamos a injeção de um contexto dentro do DaggerHilt */
        @ApplicationContext context: Context
    ) = context.getSharedPreferences( "fake_ecommerce_manager", Context.MODE_PRIVATE ) // pasando o nome do arquivo que queremos criar e em modo privado, isso injetará automaticamente na classe "SessionManager"


    /* Provendo uma instância do firebase para o nosso app*/
    @Provides
    @Singleton
    fun providesStorageReference(): StorageReference =
        FirebaseStorage.getInstance().reference


    @Provides
    fun providesBaseUrl() = "https://api.escuelajs.co/api/v1/"

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

    @Provides
    @Singleton
    fun provideUserRepository( userDataSource: UserDataSource ) =
        UserRepository( userDataSource )

    @Provides
    @Singleton
    fun provideProductsDataSource( apiService: ApiService ) =
        ProductsDataSource( apiService )

    @Provides
    @Singleton
    fun provideProductsRepository( productsDataSource: ProductsDataSource ) =
        ProductsRepository( productsDataSource )
}

