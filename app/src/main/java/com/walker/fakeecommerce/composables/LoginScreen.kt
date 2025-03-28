package com.walker.fakeecommerce.composables

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.walker.fakeecommerce.components.HeadingTextComponent
import com.walker.fakeecommerce.components.MyTextFieldComponent
import com.walker.fakeecommerce.components.NormalTextComponent
import com.walker.fakeecommerce.components.PasswordTextFieldComponent
import com.walker.fakeecommerce.data.login.LoginViewModel
import com.walker.fakeecommerce.R
import com.walker.fakeecommerce.components.ButtonComponent
import com.walker.fakeecommerce.components.ClickableLoginTextComponent
import com.walker.fakeecommerce.components.DividerTextComponent
import com.walker.fakeecommerce.data.login.LoginUIEvent
import com.walker.fakeecommerce.navigation.Screen

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    /* Para observar os estados, vamos efetuar a aquisição deles */
    val alreadyLogged = loginViewModel.loginUIState.value.alreadyLogged
    val loadingAlreadyLogged = loginViewModel.loginUIState.value.loadingAlreadyLogged
    val logoutUser = loginViewModel.logoutUser.value

    fun onSuccess(){
        /* Centraliza o evento de onSuccess que é navegar para dentro da tela de "ProductsScreen". Isso ocorre
         em dois momentos: Quando o usuário loga com sucesso e quando ele já está logado. */

         navController.navigate(Screen.PRODUCTS_SCREEN.name) {
            popUpTo(0)
        }
    }

    /* Criando a lógica para decidir a navegação */
    if (logoutUser){ // caso o usuário tenha que ser deslogado, voltamos para "LoginScreen" e fazemos um popUp
        navController.navigate(Screen.LOGIN_SCREEN.name){
            popUpTo(
                navController.graph.findStartDestination().id // volta pra tela inicial do app
            ){
                saveState = true
            }
            launchSingleTop = true // faz o popUpTo() de todas as outras telas para que quando o usuário clicar em voltar ele não consiga acessar nenhuma outra tela que não a "LoginScreen", já que ele foi deslogado.
            restoreState = true
        }
    }

    if (alreadyLogged){
        onSuccess()
    }

    /*
     Não mostra o contéudo da tela de "LoginScreen" caso o "loadingAlreadyLogged" ainda esteja como "true",
     pois significa que ainda está fazendo o cálculo se o usuário está logado ou não.
     Também não motramos a tela de "LoginScreen" se "alreadyLogged" for "true", já que o usuário já
     está logado.
    */
    if ( !loadingAlreadyLogged && !alreadyLogged ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(28.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    NormalTextComponent(value = stringResource(id = R.string.login))
                    HeadingTextComponent(value = stringResource(id = R.string.welcome))
                    Spacer(modifier = Modifier.height(20.dp))

                    MyTextFieldComponent(
                        labelValue = stringResource(id = R.string.email),
                        Icons.Default.Message,
                        onTextChanged = {
                            loginViewModel.onEvent(LoginUIEvent.EmailChanged(it))
                        },
                        errorStatus = loginViewModel.loginUIState.value.emailError
                    )

                    PasswordTextFieldComponent(
                        labelValue = stringResource(id = R.string.password),
                        Icons.Default.Lock,
                        onTextSelected = {
                            loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it))
                        },
                        errorStatus = loginViewModel.loginUIState.value.passwordError
                    )

                    Spacer(modifier = Modifier.height(70.dp))

                    if ( !loginViewModel.loginInProgress.value ) {
                        ButtonComponent(
                            value = stringResource(id = R.string.login),
                            onButtonClicked = {
                                loginViewModel.onEvent(
                                    LoginUIEvent.LoginButtonClicked(
                                        onSuccess = {

                                            onSuccess()

                                            /* navController.navigate(Screen.PRODUCTS_SCREEN.name) {
                                        popUpTo(0)
                                    } */
                                        },
                                        onFailure = {
                                            Toast.makeText(
                                                context,
                                                "Login falhou! Usuário ou senha incorreto(s)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    ))
                            },
                            isEnabled = loginViewModel.allValidationsPassed.value,
                            imageVector = Icons.Default.Login
                        )
                    }else{
                        CircularProgressIndicator() // caso esteja sendo feito o "loginInProgress" vai mostrar o loadindiator, casso não, vai mostrar o botão de login
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    DividerTextComponent()

                    ClickableLoginTextComponent(tryingToLogin = false, onTextSelected = {
                        navController.navigate(Screen.SIGNUP_SCREEN.name)
                    })
                }
            }

            /*if (loginViewModel.loginInProgress.value) {
                CircularProgressIndicator()
            }*/

        }
    }

}