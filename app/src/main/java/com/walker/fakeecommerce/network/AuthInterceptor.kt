package com.walker.fakeecommerce.network

import android.util.Log
import com.walker.fakeecommerce.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/* Aula 3.5,
   Cria o interceptor que será usado para inserir o token do usuário
   na requisição de perfil do usuário para a API.
   Sempre usa-se um interceptor quando se precisa adiconar algum
   "header" na requisição.
   Todas as requests do Retrofit passarão por esse interceptor que assim que ele tiver a request
   ele adicionará o token.
   */
class AuthInterceptor constructor(
    private val sessionManager: SessionManager // não é injetado, passaremos uma instância para ele.
): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        /* Aqui dentro interceptamos a requisição e adicionamos o token do usuário logado nela, assim, a API devolverá os dados dele para mostrarmos na telade perfil dele.*/
        val requestBuilder = chain.request().newBuilder()

        sessionManager.readToken()?.let{
            requestBuilder.addHeader("Authorization", "Bearer $it")
            Log.d("Token", "o valor do token é: $it")
        }

        return chain.proceed( requestBuilder.build() ) /* retornamos a cadeia da request ao normal para que o retrofit possa dar continuidade e efetuar a requisição para a API. */
    }

}