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

    /* Aula 3.7, criando variáveis "currentOffset" e "currentLimit". */
    private var currentOffset = 0
    private var currentLimit = 10


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
                        //allProducts = listOf()
                        /* Inicia a lista de todos os produtos vazia. Toodo o processo deste bloco é um modo da UI se
                        preparar e dizer ao usuário que está sendo feito o loading. Aula 3.7, apaga essa liha para não zerar a lista de produtos mais.  */
                    )
                    /* Adquirindo a respost da API com os produtos. Sendo um sucesso, passamos o resultado com os produtos para
                       dentro do estado. */
                    val response = productsRepository.getProducts(currentOffset, currentLimit) /* Aula 3.7, alterado implementar paginação dos produtos. */

                    if (response.isSuccessful){

                        /* Aula 3.7, criamos um list para adicionar os produtos novos da paginação sem perder os que estavam pela
                           substituição. Ou seja, adicionamos mais junto com os que já existiam. */
                        val currentProducts = productsUIState.value.allProducts.toMutableList() // criando uma lista mutável com todos os produtos.
                        currentProducts.addAll(response.body() ?: emptyList()) // Adiciona os novos itens vindos, se vier nulo, adiciona uma lista vazia, veja o operador de Elvis "?:"

                        productsUIState.value = productsUIState.value.copy(
                            //allProducts = response.body() ?: emptyList(), // operador Elvis, colocar o lado esquerdo caso não "null" e o da direita caso "null"
                            allProducts = currentProducts,
                            productsAreLoading = false, // informa para a UI que os produtos não estão mais carregando
                            productsLoadingError = false,
                        )
                    }else{
                        productsUIState.value = productsUIState.value.copy(
                            // allProducts = emptyList(), /* Aula 3.7, retirado para não mostrar a tela branca entre uma carga e outra da paginação de produtos. */
                            productsAreLoading = false, // informa para a UI que os produtos não estão mais carregando
                            productsLoadingError = true, // caso apresente erro ao carregar os produtos da API
                        )
                    }

                    /* Aula 3.7, somando um ao outro para que tenhamos primeiro de 0 a 10, depois 10 a 20 e assim sucessivamente. */
                    currentOffset += currentLimit
                }
            }
        }
    }
}