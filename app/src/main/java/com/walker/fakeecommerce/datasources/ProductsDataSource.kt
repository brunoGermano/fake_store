package com.walker.fakeecommerce.datasources

import com.walker.fakeecommerce.network.ApiService
import javax.inject.Inject

/* Classe de prover dados relacionados ao produto */

class ProductsDataSource @Inject constructor(
    private val apiService: ApiService
){
    suspend fun getProducts(offset: Int, limit: Int) = apiService.getProducts(offset, limit)
}