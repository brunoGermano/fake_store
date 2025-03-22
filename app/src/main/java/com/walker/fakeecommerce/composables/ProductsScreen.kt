package com.walker.fakeecommerce.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.walker.fakeecommerce.R
import com.walker.fakeecommerce.components.ButtonComponent
import com.walker.fakeecommerce.data.products.ProductsUIEvent
import com.walker.fakeecommerce.data.products.ProductsViewModel
import com.walker.fakeecommerce.model.Product
import com.walker.fakeecommerce.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//fun ProductsScreen(products: List<Product>, navController: NavHostController, viewModel: ProductsViewModel) {
fun ProductsScreen( navController: NavHostController, viewModel: ProductsViewModel) {
    /* Precisamos lançar o evento de "GetProducts" de algum lugar e também observar o estado dele.
       Para isso criamos a variável que vai consumir do "productsUIState" da variável "allProducts"
       que será passado para o composable da "ProductsList" e será consumido pelo "LazyColumn".
       Depois é só chamar o "GetProducts" do "ProductsViewModel" */
    val products = viewModel.productsUIState.value.allProducts

    /* Criando os outros estados da "ViewModel" que pega da "UISate", que é quem mantém os estados. O
      lance da ideia de construção do composable que monitora algo para saber se deve efetuar a recomposição.*/
    val isLoading = viewModel.productsUIState.value.productsAreLoading
    val hasError = viewModel.productsUIState.value.productsLoadingError

    /*Chama-se o "LaunchedEffect" passando "Unit" para que se execute apenas 1 vez o bloco dele e lançar o
      evento do "GetProducts" para o "viewMoldel" */
    LaunchedEffect(Unit){

        /* Colocado o if para não deixar executar o "GetProducts" toda vez que voltarmos para esta tela que
           é a de "ProductsScreen". Só queremos fazer a carga só na primeira vez que é quando o "products"
           esta com empty/vazio, assim toda vez que o usuário interagir com outras telas e voltar para esta,
           não fará novamente a carga dos dados da API. Isso tendo em vista que o bloco do "LaunchedEffect"
           executa toda vez que o composable é recomposto. */
        if( products.isEmpty() ) {
            viewModel.onEvent(ProductsUIEvent.GetProducts)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.PROFILE_SCREEN.name)
                    }) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.SHOPPING_CART.name)
                    }) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        },
        content = {

            /* Verifica se carregou com sucesso os produtos da API */
            if (isLoading){ // mostra o circularProgressIndicator
                Column (
                    modifier = Modifier.padding(it).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CircularProgressIndicator()
                }
            }

            if (hasError){
                Column (
                    modifier = Modifier.padding(it).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = stringResource(id = R.string.error_loading))
                    ButtonComponent( // botão para recaregar produtos
                        value = stringResource(id = R.string.retry),
                        onButtonClicked = {
                            viewModel.onEvent( ProductsUIEvent.GetProducts ) /* chama o evento de click, nele chama-se
                            a "ProductsUIEvent" que chama o método "GetProducts" para buscar os produtos. */
                        },
                        imageVector = Icons.Default.ArrowCircleDown
                    )
                }
            }

            if ( !isLoading && !hasError ) { // neste caso pode mostrar a lista de produtos pois não deu erro ao buscar da API e também já terminou de buscá-los

                ProductList(products, it, onItemClick = { product ->
                    viewModel.onEvent(
                        ProductsUIEvent.OpenProductDetail(
                            product,
                            onNavigate = {
                                navController.navigate("product_detail_screen")
                            }
                        )
                    )
                })
            }
        },

    )
}

@Composable
fun ProductList(products: List<Product>, paddingValues: PaddingValues, onItemClick: (Product) -> Unit) {
    Column (
        modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Text(text = "Produtos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 25.dp, bottom = 15.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(products.chunked(2)) { productList ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (product in productList) {
                        Column(
                            Modifier
                                .weight(1f)
                                .padding(10.dp)
                        ) {
                            ProductItem(product = product, onItemClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onItemClick: (Product) -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable(onClick = { onItemClick(product) })
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SubcomposeAsyncImage(
                model = product.images?.get(0), // pega sempre o primeiro item da lista de imagens que vem com umas 3 delas
                loading = {
                    CircularProgressIndicator()
                },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = product.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.price, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

        }
    }
}