package com.walker.fakeecommerce.utils

import android.content.SharedPreferences
import com.walker.fakeecommerce.model.AccessToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/* Contém os métodos necessários pra gerenciar a sessão
   usuário
*/

class SessionManager  @Inject constructor(
   private val sharedPreferences: SharedPreferences
){
    private val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
    private val LOGIN_DATE_KEY = "LOGIN_DATE_KEY" // nosso token só irá durar 20 dias

    /* Método para escrever o Token no arquivo do sharedPreferences */
    fun writeToken( accessToken: AccessToken ): Boolean {
        /* Vai receber nosso objeto token e retornar um boolean */
       return try {
            val sharedPreferencesEdit = sharedPreferences.edit() // iniciando uma edição no cursor do arquivo

            sharedPreferencesEdit.putString( ACCESS_TOKEN_KEY, accessToken.accessToken ) // escrevendo no arquivo do sharedPreferences

            val simpleFormatDate = SimpleDateFormat( "yyyy-MM-dd", Locale.ENGLISH ) // pegar a data atual para saber quando esse token foi emitido
            val date = simpleFormatDate.format(Date()) // criando a data com o formato acima

            sharedPreferencesEdit.putString(LOGIN_DATE_KEY, date) // salvar a data no arquivo do sharedPreferences
            /* Aplicando as mudanças no sharedPreferences */
            sharedPreferencesEdit.apply()

            true

        } catch ( e: Exception ){
            false
        }
    }

    /* Método para ler o Token no arquivo do sharedPreferences */
    fun readToken() = sharedPreferences.getString( ACCESS_TOKEN_KEY, null ) // retorna a chave do token, se não tiver nada retorna null

    fun readDate() = sharedPreferences.getString( LOGIN_DATE_KEY, null )

    /* Método para limpar o SharedPreferences expirados os 20 dias */
    fun logout() = sharedPreferences.edit().clear().apply()


}