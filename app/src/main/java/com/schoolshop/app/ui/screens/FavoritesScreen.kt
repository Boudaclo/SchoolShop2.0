package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.ProductRow
import com.schoolshop.app.ui.components.ScreenTopBar
import com.schoolshop.app.ui.theme.Muted

@Composable
fun FavoritesScreen(vm: ShopViewModel, onBack: () -> Unit, onProduct: (Int) -> Unit) {
    val favs = vm.products.filter { it.id in vm.favorites }
    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Mes favoris", onBack)
        if (favs.isEmpty()) {
            Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                Text("♡", fontSize = 56.sp, color = Muted)
                Text("Aucun favori pour le moment", color = Muted)
            }
        } else {
            LazyColumn {
                items(favs, key = { it.id }) { p -> ProductRow(p, { onProduct(p.id) }, { vm.add(p) }) }
            }
        }
    }
}
