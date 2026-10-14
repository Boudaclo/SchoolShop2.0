package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.data.DeliveryMode
import com.schoolshop.app.data.PaymentMode
import com.schoolshop.app.data.formatFcfa
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.*
import com.schoolshop.app.ui.theme.*

@Composable
fun CheckoutScreen(vm: ShopViewModel, onBack: () -> Unit, onValidate: () -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var draft by remember { mutableStateOf(vm.address) }

    if (editing) {
        AlertDialog(
            onDismissRequest = { editing = false },
            title = { Text("Adresse de livraison") },
            text = { OutlinedTextField(draft, { draft = it }) },
            confirmButton = { TextButton({ vm.saveAddress(draft); editing = false }) { Text("Enregistrer") } },
            dismissButton = { TextButton({ editing = false }) { Text("Annuler") } },
        )
    }

    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Livraison", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            SectionTitle("Adresse de livraison")
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, null, tint = Ink)
                Text(vm.address, Modifier.padding(start = 8.dp), fontSize = 14.sp)
            }
            TextButton({ draft = vm.address; editing = true }) { Text("Modifier") }

            SectionTitle("Mode de livraison")
            DeliveryMode.entries.forEach { m ->
                OptionRow(
                    selected = vm.deliveryMode == m,
                    title = m.label,
                    subtitle = m.detail,
                    trailing = if (m.fee == 0) "Gratuit" else formatFcfa(m.fee),
                ) { vm.deliveryMode = m }
            }

            SectionTitle("Mode de paiement")
            PaymentMode.entries.forEach { m ->
                OptionRow(selected = vm.paymentMode == m, title = m.label, subtitle = null, trailing = "") {
                    vm.paymentMode = m
                }
            }
        }
        Column(Modifier.padding(16.dp)) {
            SummaryRow("Total", formatFcfa(vm.total), bold = true)
            Spacer(Modifier.height(8.dp))
            PrimaryButton("Valider la commande", onValidate)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, Modifier.padding(top = 16.dp, bottom = 8.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp)
}

@Composable
private fun OptionRow(selected: Boolean, title: String, subtitle: String?, trailing: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected, onClick)
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp)
            if (subtitle != null) Text(subtitle, fontSize = 12.sp, color = Muted)
        }
        if (trailing.isNotEmpty()) Text(trailing, fontSize = 13.sp)
    }
}
