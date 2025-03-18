package com.walker.fakeecommerce.data.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walker.fakeecommerce.repositories.UserRepository
import com.walker.fakeecommerce.utils.SessionManager
import com.walker.fakeecommerce.utils.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val TAG = LoginViewModel::class.simpleName

    var loginUIState = mutableStateOf( LoginUIState() )

    var allValidationsPassed = mutableStateOf(false)

    var loginInProgress = mutableStateOf(false)

    /* Cria um objeto LogoutUser que será um estado para ser observado pela UI e ela poder fazer o evento de navegação */
    var logoutUser = mutableStateOf(false)

    /* Verificar se o usuário já fez o login ou não. Se tiver feito a gente pula a tela de login
       e vai direto para o aplicativo. */
    init {
        loginUIState.value.loadingAlreadyLogged = true
        val tokenExists = sessionManager.readToken() != null // verifica se o token existe ou não e se ele é diferente de null

        // Se o token existe, o usuário já está logado no app
        if (tokenExists){
            val tokenDate = sessionManager.readDate()
            /* Se a data de acesso do token for maior que 20 dias, então já expirou . Observação importante
              e que essa verificação é sempre feita em um endpoint do lado do backend no webservice, porém, nossa API
              não tem um endpoint para isso e, portanto, fazemos isso do lado do cliente, no nosso app. */
            if ( Period.between( LocalDate.parse(tokenDate), LocalDate.now() ).days > 20 ) { // verifa o período que o token foi salvo e a data atual, e ver se ela é maior que 20 dias
                /* Detalhe: Para usar a classe "Period"  precisamos mudar, no gradle nível de app, o min SDK de 24, que estava, para 26 pra usá-lo */
                // Se já expirou o login, então fotça o logout
                onEvent(LoginUIEvent.LogoutUser) // LogoutUser é um objeto! vái dentro da classe "LoginUIEvent" que verá a observação.
                logoutUser.value = true // caso já tenha passado a data de expiração
            }else{
                /* Se não passou a gente faz o alreadLogged porque já foi logado */
                loginUIState.value.alreadyLogged = true // Isso avisa a UI através deste estado como true. A UI que detém o poder de navController para nagevar entre as telas
            }
            loginUIState.value.loadingAlreadyLogged = false
        }

        /* linha adicionada pelo allan para corrigir o problema do primeiro login,
           onde ainda nao temos o token de acesso salvo pelo session manager, parece
           que a Anny não tinha previsto esta situação. */
        loginUIState.value.loadingAlreadyLogged = false

    }

    fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.EmailChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    email = event.email
                )
                validateLoginUIDataWithRules()
            }

            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
                validateLoginUIDataWithRules()
            }

            is LoginUIEvent.LoginButtonClicked -> {
                viewModelScope.launch {
                    /* PARAMOS AQUI!!!!! MINUTO 4:44 */
                    login(event.onSuccess, event.onFailure)
                    /* validateLoginUIDataWithRules() */

                }
            }

            /* Incluindo o evento do LogoutUser aqui no método "onEvent()" para ele saber o que tem que fazer */
            is LoginUIEvent.LogoutUser -> { // quando o evento for "LogoutUser", executamos o bloco abaixo.
                sessionManager.logout()
                logoutUser.value = true
            }

        }
    }

    private fun validateLoginUIDataWithRules() {
        val emailResult = Validator.validateEmail(
            email = loginUIState.value.email
        )

        val passwordResult = Validator.validatePassword(
            password = loginUIState.value.password
        )

        loginUIState.value = loginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = passwordResult.status
        )

        allValidationsPassed.value = emailResult.status && passwordResult.status

    }

    private suspend fun login(onSuccess: () -> Unit, onFailure: () -> Unit) {

        loginInProgress.value = true
        val email = loginUIState.value.email
        val password = loginUIState.value.password

        val result = userRepository.postLogin( email, password )

        if(result.isSuccessful){
            result.body()?.let{
                onSuccess() // responsável por trocar da tela de login para a tela do aplicativo

                /* Escrevendo o token recebido do "result.body()" no sharedPreferences */
                sessionManager.writeToken( it )

            } ?: run{ // operador Elvis, executa o que está a esquerda se não for nulo, caso seja, executa o que está a direita dele
                onFailure()
            }
        } else{
            onFailure()
        }

    /*
        if (email == "test@test.com" && password == "teste123") {
            onSuccess()
        } else {
            onFailure()
        }
    */

        loginInProgress.value = false
    }

}