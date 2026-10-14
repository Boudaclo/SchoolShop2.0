package com.schoolshop.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.data.local.PaymentMethodEntity
import com.schoolshop.app.data.local.summary
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.PrimaryButton
import com.schoolshop.app.ui.components.ScreenTopBar
import com.schoolshop.app.ui.theme.*

private const val CARD = "Carte bancaire"
private val providers = listOf("Orange Money", "Moov Money", CARD)

@Composable
fun PaymentsScreen(vm: ShopViewModel, onBack: () -> Unit) {
    var dialogOpen by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<PaymentMethodEntity?>(null) }

    if (dialogOpen) {
        PaymentDialog(
            onDismiss = { dialogOpen = false },
            onSave = { provider, detail, makeDefault ->
                vm.savePayment(
                    PaymentMethodEntity(
                        type = if (provider == CARD) "CARD" else "MOBILE_MONEY",
                        provider = provider,
                        detail = detail,
                        isDefault = makeDefault,
                    )
                )
                dialogOpen = false
            },
        )
    }
    toDelete?.let { p ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Supprimer ce moyen de paiement ?") },
            text = { Text(p.summary()) },
            confirmButton = { TextButton({ vm.deletePayment(p.id); toDelete = null }) { Text("Supprimer") } },
            dismissButton = { TextButton({ toDelete = null }) { Text("Annuler") } },
        )
    }

    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Mes moyens de paiement", onBack)
        if (vm.payments.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
                Text("Aucun moyen de paiement enregistré", color = Muted)
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(vm.payments, key = { it.id }) { p ->
                    Card(colors = CardDefaults.cardColors(containerColor = BlueLight)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(p.summary(), fontWeight = FontWeight.SemiBold)
                                if (p.isDefault) Text("Par défaut", color = Blue, fontSize = 12.sp)
                                else TextButton({ vm.setDefaultPayment(p.id) }, contentPadding = PaddingValues(0.dp)) {
                                    Text("Définir par défaut", fontSize = 12.sp)
                                }
                            }
                            IconButton({ toDelete = p }) { Icon(Icons.Default.Delete, "Supprimer") }
                        }
                    }
                }
            }
        }
        PrimaryButton("Ajouter un moyen de paiement", { dialogOpen = true }, Modifier.padding(16.dp))
    }
}

@Composable
private fun PaymentDialog(onDismiss: () -> Unit, onSave: (String, String, Boolean) -> Unit) {
    var provider by remember { mutableStateOf(providers[0]) }
    var value by remember { mutableStateOf("") }
    var makeDefault by remember { mutableStateOf(false) }
    val isCard = provider == CARD
    val valid = if (isCard) value.length == 4 else value.length >= 8

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau moyen de paiement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    providers.forEach { p ->
                        FilterChip(selected = provider == p, onClick = { provider = p; value = "" }, label = { Text(p) })
                    }
                }
                OutlinedTextField(
                    value = value,
                    onValueChange = { v -> value = v.filter { it.isDigit() }.take(if (isCard) 4 else 12) },
                    label = { Text(if (isCard) "4 derniers chiffres de la carte" else "Numéro de téléphone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                if (isCard) {
                    Text("Seuls les 4 derniers chiffres sont enregistrés, jamais le numéro complet.", fontSize = 12.sp, color = Muted)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(makeDefault, { makeDefault = it })
                    Text("Moyen de paiement par défaut")
                }
            }
        },
        confirmButton = { TextButton({ onSave(provider, value, makeDefault) }, enabled = valid) { Text("Enregistrer") } },
        dismissButton = { TextButton(onDismiss) { Text("Annuler") } },
    )
}
