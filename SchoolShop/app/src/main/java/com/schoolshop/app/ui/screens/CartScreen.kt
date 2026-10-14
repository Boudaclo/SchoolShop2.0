package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.data.formatFcfa
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.*
import com.schoolshop.app.ui.theme.*

@Composable
fun CartScreen(vm: ShopViewModel, onCheckout: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Panier")
        if (vm.cart.isEmpty()) {
            Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                Text("🛒", fontSize = 64.sp)
                Text("Votre panier est vide", color = Muted, modifier = Modifier.padding(top = 8.dp))
            }
            return@Column
        }
        LazyColumn(Modifier.weight(1f)) {
            items(vm.cart, key = { it.product.id }) { item ->
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    ProductImage(item.product, 72.dp)
                    Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                        Text(item.product.name, fontWeight = FontWeight.SemiBold)
                        Text(formatFcfa(item.product.price), color = Blue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        QuantityStepper(item.quantity) { vm.setQty(item.product.id, it) }
                    }
                    IconButton(onClick = { vm.remove(item.product.id) }) {
                        Icon(Icons.Default.Delete, "Supprimer", tint = Muted)
                    }
                }
                HorizontalDivider(color = Line, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
        Column(Modifier.padding(16.dp)) {
            SummaryRow("Sous-total", formatFcfa(vm.subtotal))
            SummaryRow("Livraison", formatFcfa(vm.deliveryFee))
            HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 8.dp))
            SummaryRow("Total", formatFcfa(vm.total), bold = true)
            Spacer(Modifier.height(12.dp))
            PrimaryButton("Passer la commande", onCheckout)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, Modifier.weight(1f), fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = if (bold) Blue else Ink)
    }
}
