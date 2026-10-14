package com.schoolshop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.*
import com.schoolshop.app.ui.theme.*

@Composable
fun HomeScreen(
    vm: ShopViewModel,
    onCategory: (String) -> Unit,
    onProduct: (Int) -> Unit,
    onCart: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val results = if (query.isBlank()) emptyList()
    else vm.products.filter { it.name.contains(query, ignoreCase = true) }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            ScreenTopBar("SchoolShop") {
                IconButton(onClick = onCart) {
                    BadgedBox(badge = { if (vm.cartCount > 0) Badge { Text("${vm.cartCount}") } }) {
                        Icon(Icons.Default.ShoppingCart, "Panier")
                    }
                }
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Rechercher un produit...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(12.dp))
        }

        if (query.isNotBlank()) {
            if (results.isEmpty()) item { Text("Aucun produit trouvé.", Modifier.padding(16.dp), color = Muted) }
            items(results) { p -> ProductRow(p, { onProduct(p.id) }, { vm.add(p) }) }
            return@LazyColumn
        }

        item {
            Box(
                Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(RoundedCornerShape(18.dp))
                    .background(Blue).padding(18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Préparez la rentrée en toute sérénité !", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Des fournitures scolaires à prix imbattables", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { onCategory("cahiers") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Blue),
                            shape = RoundedCornerShape(20.dp),
                        ) { Text("Voir les offres", fontSize = 12.sp) }
                    }
                    Text("🎒", fontSize = 64.sp)
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                vm.categories.take(4).forEach { c ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onCategory(c.id) },
                    ) {
                        EmojiTile(c.emoji, 60.dp)
                        Spacer(Modifier.height(4.dp))
                        Text(c.name.substringBefore(" &"), fontSize = 12.sp, color = Ink)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Produits populaires", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Voir tout", color = Blue, fontSize = 13.sp, modifier = Modifier.clickable { onCategory("cahiers") })
            }
            Spacer(Modifier.height(8.dp))
        }

        items(vm.products.filter { it.isPopular }.chunked(2)) { row ->
            Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                row.forEach { p ->
                    ProductCard(p, { onProduct(p.id) }, { vm.add(p) }, Modifier.weight(1f).padding(6.dp))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
