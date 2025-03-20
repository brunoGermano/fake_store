package com.walker.fakeecommerce.repositories

import com.walker.fakeecommerce.datasources.ProductsDataSource
import javax.inject.Inject

class ProductsRepository @Inject constructor(
    private val productsDataSource: ProductsDataSource
) {
    suspend fun getProducts() = productsDataSource.getProducts()
}