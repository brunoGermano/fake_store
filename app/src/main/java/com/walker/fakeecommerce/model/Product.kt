package com.walker.fakeecommerce.model


data class Product(
    val id: Int,

    //val image: String,
    //val images: List<String>? = null, //  A API traz mais ou menos umas 3 imagens para cada produto
    val images: List<String>? = null, /*  A API traz mais ou menos umas 3 imagens para cada produto.
    O nome do objeto que vem no Json da API é "images" e não "image", se não estiver correto o "GsonParser",
    que é a dependência que usamos para converter os dados que vem da API, não fará a conversão, isso é
    muito IMPORTANTE. */

    val title: String,
    val description: String,
    val price: String,
    //val category: String,
    val category: Category,
    var quantity: Int = 0
)