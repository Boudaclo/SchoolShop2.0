package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.ProductRow
import com.schoolshop.app.ui.components.ScreenTopBar

@Composable
fun ProductListScreen(vm: ShopViewModel, categoryId: String, onBack: () -> Unit, onProduct: (Int) -> Unit) {
    val category = vm.categories.firstOrNull { it.id == categoryId }
    val all = vm.products.filter { it.categoryId == categoryId }
    val formats = listOf("Tous") + all.map { it.format }.distinct()
    var selected by remember { mutableStateOf("Tous") }
    val shown = if (selected == "Tous") all else all.filter { it.format == selected }

    Column(Modifier.fillMaxSize()) {
        ScreenTopBar(category?.name ?: "", onBack)
        if (formats.size > 2) {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(formats) { f ->
                    FilterChip(selected = selected == f, onClick = { selected = f }, label = { Text(f) })
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        LazyColumn {
            items(shown) { p -> ProductRow(p, { onProduct(p.id) }, { vm.add(p) }) }
        }
    }
}
