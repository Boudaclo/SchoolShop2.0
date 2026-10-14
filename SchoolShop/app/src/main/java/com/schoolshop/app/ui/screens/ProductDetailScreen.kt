package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.data.formatFcfa
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.*
import com.schoolshop.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(vm: ShopViewModel, productId: Int, onBack: () -> Unit) {
    val product = vm.products.firstOrNull { it.id == productId }
    var qty by remember { mutableIntStateOf(1) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val fav = productId in vm.favorites
    if (product == null) {
        ScreenTopBar("", onBack)
        return
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbar) },
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                QuantityStepper(qty) { qty = it }
                Spacer(Modifier.width(12.dp))
                PrimaryButton("Ajouter au panier", {
                    vm.add(product, qty)
                    scope.launch { snackbar.showSnackbar("Ajouté au panier") }
                })
            }
        },
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState())) {
            ScreenTopBar("", onBack) {
                IconButton(onClick = { vm.toggleFavorite(productId) }) {
                    Icon(if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favori", tint = if (fav) Color.Red else Ink)
                }
            }
            ProductImage(product, 200.dp, Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(200.dp))
            Column(Modifier.padding(16.dp)) {
                Text(product.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text(product.format, color = Muted)
                Spacer(Modifier.height(8.dp))
                Text(formatFcfa(product.price), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Blue)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { i ->
                        Icon(Icons.Default.Star, null, Modifier.size(18.dp), tint = if (i < product.rating.toInt()) Color(0xFFFFB400) else Line)
                    }
                    Text("  (${product.reviews} avis)", fontSize = 12.sp, color = Muted)
                }
                Spacer(Modifier.height(16.dp))
                Text("Description", fontWeight = FontWeight.Bold)
                Text(product.description, color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Text("Caractéristiques", fontWeight = FontWeight.Bold)
                product.features.forEach { Text("• $it", color = Muted, fontSize = 13.sp) }
            }
        }
    }
}
