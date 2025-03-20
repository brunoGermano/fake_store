package com.walker.fakeecommerce.data.products

import com.walker.fakeecommerce.model.Product

sealed class ProductsUIEvent{

    /* Adicionando um evento para os produtos que pode ser
       um objeto, "object", pois ele não terá parâmetro. Assim,
       podemos mapear esse evento e também consumir
       ele a paratir da UI. */
    object GetProducts: ProductsUIEvent()

    data class OpenProductDetail(val product: Product, val onNavigate: () -> Unit): ProductsUIEvent()
    data class UpdateProductQuantity(val product: Product?, val quantity: Int): ProductsUIEvent()
}