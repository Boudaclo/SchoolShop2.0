package com.schoolshop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.ui.components.OutlineButton
import com.schoolshop.app.ui.components.ScreenTopBar
import com.schoolshop.app.ui.theme.*

@Composable
fun AccountScreen(onOrders: () -> Unit) {
    val items: List<Pair<ImageVector, String>> = listOf(
        Icons.AutoMirrored.Filled.ReceiptLong to "Mes commandes",
        Icons.Default.LocationOn to "Mes adresses",
        Icons.Default.CreditCard to "Mes moyens de paiement",
        Icons.Default.Favorite to "Mes favoris",
        Icons.Default.SupportAgent to "Aide & support",
        Icons.Default.Info to "À propos",
    )
    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Mon compte") {
            IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Paramètres") }
        }
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(CircleShape).background(BlueLight), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Blue)
            }
            Column(Modifier.padding(start = 14.dp)) {
                Text("David Bouda", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("david@gmail.com", color = Muted, fontSize = 13.sp)
            }
        }
        items.forEach { (icon, label) ->
            Row(
                Modifier.fillMaxWidth()
                    .clickable(enabled = label == "Mes commandes", onClick = onOrders)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(icon, null, tint = Ink)
                Text(label, Modifier.weight(1f).padding(start = 16.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Muted)
            }
            HorizontalDivider(color = Line, modifier = Modifier.padding(horizontal = 16.dp))
        }
        Spacer(Modifier.weight(1f))
        OutlineButton("Se déconnecter", {}, Modifier.padding(16.dp))
    }
}
