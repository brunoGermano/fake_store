package com.walker.fakeecommerce.data.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.walker.fakeecommerce.model.Profile
import com.walker.fakeecommerce.repositories.UserRepository
import com.walker.fakeecommerce.utils.SessionManager
import com.walker.fakeecommerce.utils.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
): ViewModel() {

    private val TAG = ProfileViewModel::class.simpleName

    var profileUIState = mutableStateOf(ProfileUIState())

    var logoutUser = mutableStateOf(false)

    var allValidationsPassed = mutableStateOf(false)

   /* val profile = Profile(
        id = "123",
        email = "test@test.mail",
        name = "Test",
        avatar = ""
    )*/

    fun onEvent(event: ProfileUIEvents) {
        when (event) {
            is ProfileUIEvents.DeleteAccount -> {

                /* Lançar um evento através do "viewModelScope" que vai pegar o perfil do usuário e passar para a requisição de deleção apenas o "id".*/
                viewModelScope.launch {
                    profileUIState.value.profile?.id?.let {
                        val result = userRepository.deleteProfile(it) // Note que o objeto "it" da lambda "let" tem o valor do "id" que é o atributo acessado à esquerda do "let".
                        if (result.isSuccessful){
                            sessionManager.logout() /* tiramos o usuário da tela, limpando os dados dele do arquivo SharedPreferences. Assim, deslogamos ele, pois como ele foi deletado, não faz mais sentido deixá-lo logado. */
                            logoutUser.value = true /* muda o estado da "logoutUser", assim faz a navegação para fora da tela de produtos. */
                        }
                    }
                }

            }
            is ProfileUIEvents.GetProfile -> {
                profileUIState.value = profileUIState.value.copy(
                    profileIsLoading = true
                )
                viewModelScope.launch {
                    //delay(500) // Aula 3.5, Usando o "getProfile" que foi consumido da API

                    val result = userRepository.getProfile()
                    if (result.isSuccessful){
                        val profileResult = result.body()
                        profileUIState.value = profileUIState.value.copy(
                            profile = profileResult,
                            profileError =  false,
                            nameField = profileResult?.name ?: "-", // Operador de Elvis aqui "?:"
                            profileIsLoading = false
                        )
                    }else{ // caso não retorne o perfil do usuário executa o else
                        profileUIState.value = profileUIState.value.copy(
                            profile = null,
                            profileError =  true, // colocar o erro como "true"
                            profileIsLoading = false
                        )
                    }



                }
            }

            is ProfileUIEvents.Logout -> {
                logoutUser.value = true
                sessionManager.logout() // precisa limpar o cache do log do sessionManager e realmente fazer o logout do usuário na tela de perfil dele
            }

            is ProfileUIEvents.EditProfile -> {
                editProfile()
            }

            is ProfileUIEvents.HasNameChanged -> {
                profileUIState.value = profileUIState.value.copy(
                    nameField = event.name
                )

                validateProfileUIDataWithRules()
            }

            is ProfileUIEvents.CleanProfile -> {
                profileUIState.value = profileUIState.value.copy(
                    profile = null,
                    profileIsLoading = false,
                    profileError =  false
                )
            }
        }
    }

    private fun validateProfileUIDataWithRules() {
        val nameResult = Validator.validateName(
            name = profileUIState.value.nameField
        )

        profileUIState.value = profileUIState.value.copy(
            nameFieldError = nameResult.status
        )

        allValidationsPassed.value = nameResult.status
    }

    private fun editProfile() { // função para editar o perfil do usuário

        profileUIState.value = profileUIState.value.copy(
            profileIsLoading = true,
            profileError = false
        )
        viewModelScope.launch {
            // delay(500)     /* Aula 3.6 */
            profileUIState.value.profile?.let { /* Agora, usará o dado vindo do perfil real, em vez do fake/estático de antes que foi
            declarado acima e que está comentado. E só chama o "updateProfile" caso oobjeto "profileUIState.value.profile" não seja null.  */

                val profileToEdit = it // "it" da função lambda do let, ele tem o perfil do usuário
                profileToEdit.name = profileUIState.value.nameField // O atributo "nameField" devolve o nome que foi alterado que veio lá da tela de edição do perfil.
                val result = userRepository.updateProfile(profileToEdit) // linha que chama a API para atualizar o perfil do usuário no webservice

                if (result.isSuccessful){
                    // Se a alteração do perfil na API for um sucesso, atualizamos a "profileUIState" com o novo perfil atualizado
                    val newProfile = result.body()
                    profileUIState.value = profileUIState.value.copy( /* Passando para o "profileUIState.value" uma cópia de si próprio só que agora com o profile novo. */
                        profileIsLoading = false,
                        profile = newProfile, // novo profile atribuído, substituindo o estado anterior do profile
                        nameField = newProfile?.name ?: "", // passa o novo nome do perfil, atente no operador de elvis "?:" que coloca a string vazia caso não retorne o nome do objeto "newProfile?.name"
                        profileError = false
                    )
                }else{
                    profileUIState.value = profileUIState.value.copy(
                        profileIsLoading = false,
                        profileError = true
                    )
                }

            }
        }
    }
}