package com.walker.fakeecommerce.data.login

data class LoginUIState(
    var email  :String = "",
    var password  :String = "",

    var emailError :Boolean = false,
    var passwordError : Boolean = false,

    var alreadyLogged: Boolean = false,
    var loadingAlreadyLogged: Boolean = true // pois demorará um tempinho para detectar se ele foi logged ou não. já vem com "true" pois sempre será executada no init do viewModel

)