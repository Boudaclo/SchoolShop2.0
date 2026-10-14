package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.data.DeliveryMode
import com.schoolshop.app.data.formatFcfa
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.ScreenTopBar
import com.schoolshop.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(vm: ShopViewModel, onBack: () -> Unit) {
    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Mes commandes", onBack)
        if (vm.orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("Aucune commande pour le moment", color = Muted)
            }
            return@Column
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(vm.orders, key = { it.order.id }) { o ->
                Card(colors = CardDefaults.cardColors(containerColor = BlueLight)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Row {
                            Text(o.order.code, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Text(fmt.format(Date(o.order.createdAt)), fontSize = 12.sp, color = Muted)
                        }
                        Spacer(Modifier.height(8.dp))
                        o.items.forEach {
                            Text("${it.quantity} × ${it.productName} — ${formatFcfa(it.unitPrice * it.quantity)}", fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(DeliveryMode.valueOf(o.order.deliveryMode).label, fontSize = 12.sp, color = Muted)
                        Text("Total : ${formatFcfa(o.order.total)}", fontWeight = FontWeight.Bold, color = Blue)
                    }
                }
            }
        }
    }
}
