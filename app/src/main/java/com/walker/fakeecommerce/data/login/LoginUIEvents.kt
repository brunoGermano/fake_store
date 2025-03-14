package com.walker.fakeecommerce.data.login

sealed class LoginUIEvent{

    data class EmailChanged(val email:String): LoginUIEvent()
    data class PasswordChanged(val password: String) : LoginUIEvent()

    /* Cria o envento de LogoutUser para deslogar o usuário. Ele é um objeto, classe estática, "object", pois não temos nenhum parâmetro a ser passado */
     object LogoutUser: LoginUIEvent() // objeto criado na sintaxe de classe, assim, sabe-se que ele não é pra instancia objetos desse tipo, já que ele próprio é o objeto.

    data class LoginButtonClicked(val onSuccess: () -> Unit, val onFailure: () -> Unit) : LoginUIEvent()

}