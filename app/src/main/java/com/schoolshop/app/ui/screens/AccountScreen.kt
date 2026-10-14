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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.OutlineButton
import com.schoolshop.app.ui.components.ScreenTopBar
import com.schoolshop.app.ui.theme.*

private enum class AccountDialog { PROFILE, ABOUT, LOGOUT }

private class MenuItem(val icon: ImageVector, val label: String, val onClick: () -> Unit)

@Composable
fun AccountScreen(
    vm: ShopViewModel,
    onOrders: () -> Unit,
    onAddresses: () -> Unit,
    onPayments: () -> Unit,
    onFavorites: () -> Unit,
    onSupport: () -> Unit,
) {
    var dialog by remember { mutableStateOf<AccountDialog?>(null) }

    val items = listOf(
        MenuItem(Icons.AutoMirrored.Filled.ReceiptLong, "Mes commandes", onOrders),
        MenuItem(Icons.Default.LocationOn, "Mes adresses", onAddresses),
        MenuItem(Icons.Default.CreditCard, "Mes moyens de paiement", onPayments),
        MenuItem(Icons.Default.Favorite, "Mes favoris", onFavorites),
        MenuItem(Icons.Default.SupportAgent, "Aide & support", onSupport),
        MenuItem(Icons.Default.Info, "À propos") { dialog = AccountDialog.ABOUT },
    )

    when (dialog) {
        AccountDialog.PROFILE -> ProfileDialog(vm) { dialog = null }
        AccountDialog.ABOUT -> AboutDialog { dialog = null }
        AccountDialog.LOGOUT -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text("Se déconnecter ?") },
            text = {
                Text(
                    "Cette application n'a pas de compte en ligne : vos données (profil, panier, favoris, " +
                        "adresses, moyens de paiement et commandes) sont enregistrées sur ce téléphone. " +
                        "Se déconnecter les efface définitivement."
                )
            },
            confirmButton = { TextButton({ vm.logout(); dialog = null }) { Text("Se déconnecter") } },
            dismissButton = { TextButton({ dialog = null }) { Text("Annuler") } },
        )
        null -> {}
    }

    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Mon compte") {
            IconButton(onClick = { dialog = AccountDialog.PROFILE }) { Icon(Icons.Default.Settings, "Modifier le profil") }
        }
        Row(
            Modifier.fillMaxWidth().clickable { dialog = AccountDialog.PROFILE }.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(56.dp).clip(CircleShape).background(BlueLight), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = Blue)
            }
            Column(Modifier.padding(start = 14.dp)) {
                Text(vm.profileName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(vm.profileEmail, color = Muted, fontSize = 13.sp)
            }
        }
        items.forEach { item ->
            Row(
                Modifier.fillMaxWidth().clickable(onClick = item.onClick).padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(item.icon, null, tint = Ink)
                Text(item.label, Modifier.weight(1f).padding(start = 16.dp))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Muted)
            }
            HorizontalDivider(color = Line, modifier = Modifier.padding(horizontal = 16.dp))
        }
        Spacer(Modifier.weight(1f))
        OutlineButton("Se déconnecter", { dialog = AccountDialog.LOGOUT }, Modifier.padding(16.dp))
    }
}

@Composable
private fun ProfileDialog(vm: ShopViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(vm.profileName) }
    var email by remember { mutableStateOf(if (vm.profileEmail.contains("@")) vm.profileEmail else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mon profil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Nom") }, singleLine = true)
                OutlinedTextField(email, { email = it }, label = { Text("E-mail") }, singleLine = true)
            }
        },
        confirmButton = { TextButton({ vm.saveProfile(name.trim(), email.trim()); onDismiss() }) { Text("Enregistrer") } },
        dismissButton = { TextButton(onDismiss) { Text("Annuler") } },
    )
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val version = remember {
        runCatching { ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName }.getOrNull() ?: "1.0"
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("SchoolShop") },
        text = { Text("Tout pour bien apprendre au même endroit !\n\nVersion $version") },
        confirmButton = { TextButton(onDismiss) { Text("Fermer") } },
    )
}
