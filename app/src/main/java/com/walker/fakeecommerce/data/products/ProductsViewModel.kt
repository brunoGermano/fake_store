package com.walker.fakeecommerce.data.products

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.walker.fakeecommerce.repositories.ProductsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
): ViewModel() {

    var productsUIState = mutableStateOf(ProductsUIState())

    fun onEvent(event: ProductsUIEvent) {
        when (event) {
            is ProductsUIEvent.OpenProductDetail -> {
                productsUIState.value = productsUIState.value.copy(
                    selectedProduct = event.product
                )
                event.onNavigate()
            }
            is ProductsUIEvent.UpdateProductQuantity -> {
                val allProductsTemp =  productsUIState.value.allProducts.toMutableList()
                allProductsTemp.find { it.id == event.product?.id }?.quantity = event.quantity
                productsUIState.value = productsUIState.value.copy(
                    allProducts = allProductsTemp
                )
                if (productsUIState.value.selectedProduct?.id == event.product?.id) {
                    productsUIState.value.selectedProduct = event.product?.copy(quantity = event.quantity)
                }
            }


            /* Mapeando o evento  de "GetProducts" para que ele saiba o que deve ser feito */
            is ProductsUIEvent.GetProducts -> {
                /*Lançar o método através da coroutine scope para criar uma thread assíncrona de execução do bloco */
                viewModelScope.launch {
                    productsUIState.value = productsUIState.value.copy(
                        productsAreLoading = true, // informa para a UI que os produtos estão carregando
                        productsLoadingError = false,
                        allProducts = listOf() /* Inicia a lista de todos os produtos vazia. Toodo o processo deste bloco é um modo da UI se preparar e dizer ao usuário que está sendo feito o loading. */
                    )
                    /* Adquirindo a respost da API com os produtos. Sendo um sucesso, passamos o resultado com os produtos para
                       dentro do estado. */
                    val response = productsRepository.getProducts()

                    if (response.isSuccessful){
                        productsUIState.value = productsUIState.value.copy(
                            allProducts = response.body() ?: emptyList(), // operador Elvis, colocar o lado esquerdo caso não "null" e o da direita caso "null"
                            productsAreLoading = false, // informa para a UI que os produtos não estão mais carregando
                            productsLoadingError = false,
                        )
                    }else{
                        productsUIState.value = productsUIState.value.copy(
                            allProducts = emptyList(),
                            productsAreLoading = false, // informa para a UI que os produtos não estão mais carregando
                            productsLoadingError = true, // caso apresente erro ao carregar os produtos da API
                        )
                    }
                }
            }
        }
    }
}