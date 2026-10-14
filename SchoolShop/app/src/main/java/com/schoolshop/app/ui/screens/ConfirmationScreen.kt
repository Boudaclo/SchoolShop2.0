package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.*
import com.schoolshop.app.ui.theme.*

@Composable
fun ConfirmationScreen(vm: ShopViewModel, onOrders: () -> Unit, onHome: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Default.CheckCircle, null, Modifier.size(96.dp), tint = Green)
        Spacer(Modifier.height(16.dp))
        Text("Commande confirmée !", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "Merci pour votre achat !\nVotre commande ${vm.lastOrderId}\nest en cours de traitement.",
            textAlign = TextAlign.Center,
            color = Muted,
        )
        Spacer(Modifier.height(16.dp))
        Surface(color = BlueLight, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)) {
            Text(
                "🚚  ${vm.deliveryMode.detail}",
                Modifier.fillMaxWidth().padding(14.dp),
                color = Blue,
                fontSize = 13.sp,
            )
        }
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Voir mes commandes", onOrders)
        Spacer(Modifier.height(10.dp))
        OutlineButton("Retour à l'accueil", onHome)
    }
}
