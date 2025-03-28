package com.walker.fakeecommerce.data.signup

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.StorageReference
import com.walker.fakeecommerce.repositories.UserRepository
import com.walker.fakeecommerce.utils.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val storageReference: StorageReference,
    private val userRepository: UserRepository
): ViewModel() {

    private val TAG = SignUpViewModel::class.simpleName

    var signUpUIState = mutableStateOf(SignUpUIState())

    var allValidationsPassed = mutableStateOf(false)

    var signUpInProgress = mutableStateOf(false)

    var signUpSuccessCompleted = mutableStateOf(false)

    fun onEvent(event: SignUpUIEvent) {
        when (event) {
            is SignUpUIEvent.NameChanged -> {
                signUpUIState.value = signUpUIState.value.copy(
                    name = event.name
                )
                printState()
                validateDataWithRules()
            }

            is SignUpUIEvent.EmailChanged -> {
                signUpUIState.value = signUpUIState.value.copy(
                    email = event.email
                )
                printState()
                validateDataWithRules()
            }

            is SignUpUIEvent.PasswordChanged -> {
                signUpUIState.value = signUpUIState.value.copy(
                    password = event.password
                )
                printState()
                validateDataWithRules()
            }

            is SignUpUIEvent.RegisterButtonClicked -> {
                signUp()
                validateDataWithRules()
            }

            is SignUpUIEvent.PrivacyPolicyCheckBoxClicked -> {
                signUpUIState.value = signUpUIState.value.copy(
                    privacyPolicyAccepted = event.status
                )
                validateDataWithRules()
            }

            is SignUpUIEvent.ImageChanged -> {
                signUpUIState.value = signUpUIState.value.copy(
                    image = event.image
                )
                validateDataWithRules()
            }
        }
    }


    private fun signUp() {
        signUpInProgress.value = true

        signUpUIState.value.registerError = false

        signUpSuccessCompleted.value = false

        createUser(
            email = signUpUIState.value.email,
            password = signUpUIState.value.password,
            image = signUpUIState.value.image,
            onSuccess = {
                signUpUIState.value.registerError = false
                signUpSuccessCompleted.value = true
                signUpInProgress.value = false
            },
            onFailure = {
                signUpUIState.value.registerError = true
                signUpSuccessCompleted.value = false
                signUpInProgress.value = false
            },
            name = signUpUIState.value.name
        )
    }

    private fun validateDataWithRules() {
        val nameResult = Validator.validateName(
            name = signUpUIState.value.name
        )

        val emailResult = Validator.validateEmail(
            email = signUpUIState.value.email
        )

        val passwordResult = Validator.validatePassword(
            password = signUpUIState.value.password
        )

        val privacyPolicyResult = Validator.validatePrivacyPolicyAcceptance(
            statusValue = signUpUIState.value.privacyPolicyAccepted
        )

        val imageResult = Validator.validateImage(
            image = signUpUIState.value.image
        )

        signUpUIState.value = signUpUIState.value.copy(
            nameError = nameResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            privacyPolicyError = privacyPolicyResult.status
        )

        allValidationsPassed.value = nameResult.status && imageResult.status &&
                emailResult.status && passwordResult.status && privacyPolicyResult.status

    }


    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, signUpUIState.value.toString())
    }


    private fun createUser(
        email: String,
        password: String,
        image: Uri,
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        signUpInProgress.value = true

        val storageRefPhoto = storageReference.child("photos/$email")
        val task = storageRefPhoto.putFile(image)

        task.addOnSuccessListener { // verica se a tarefa de adicionar a imagem no firebase fou bem sucedida
            storageRefPhoto.downloadUrl.addOnSuccessListener { // verifia se o retorno da url da imagem adicionada é verdadeiro, se deu certo;
                postUser( email, password, it, name, onSuccess, onFailure ) // adiciona o novo user se tiver a url da imagem lá do firebase
            }.addOnFailureListener{
                onFailure()
            }
        }.addOnFailureListener{
            onFailure()
        }

        // postUser(email, password, "http://image.url.fake".toUri(), name, onSuccess, onFailure)
    }

    private fun postUser(
        email: String,
        password: String,
        image: Uri,
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
//            delay(500)
            val result = userRepository.postUser( name, email,password, image.toString() )

            if(result.isSuccessful){
                onSuccess()
            } else{
                onFailure()
            }

            signUpInProgress.value = false
        }
    }

}