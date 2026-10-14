package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.data.DeliveryMode
import com.schoolshop.app.data.PaymentMode
import com.schoolshop.app.data.formatFcfa
import com.schoolshop.app.data.local.summary
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.*
import com.schoolshop.app.ui.theme.*

@Composable
fun CheckoutScreen(
    vm: ShopViewModel,
    onBack: () -> Unit,
    onManageAddresses: () -> Unit,
    onManagePayments: () -> Unit,
    onValidate: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Livraison", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            SectionTitle("Adresse de livraison")
            val address = vm.defaultAddress
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, null, tint = Ink)
                Text(
                    address?.details ?: "Aucune adresse enregistrée",
                    Modifier.padding(start = 8.dp),
                    fontSize = 14.sp,
                    color = if (address == null) Muted else Ink,
                )
            }
            TextButton(onManageAddresses) { Text(if (address == null) "Ajouter une adresse" else "Modifier") }

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
            if (vm.payments.isEmpty()) {
                PaymentMode.entries.forEach { m ->
                    OptionRow(selected = vm.paymentMode == m, title = m.label, subtitle = null, trailing = "") {
                        vm.paymentMode = m
                    }
                }
                TextButton(onManagePayments) { Text("Enregistrer un moyen de paiement") }
            } else {
                vm.payments.forEach { p ->
                    OptionRow(selected = vm.checkoutPayment?.id == p.id, title = p.summary(), subtitle = null, trailing = "") {
                        vm.selectedPaymentId = p.id
                    }
                }
                TextButton(onManagePayments) { Text("Gérer mes moyens de paiement") }
            }
        }
        Column(Modifier.padding(16.dp)) {
            SummaryRow("Total", formatFcfa(vm.total), bold = true)
            if (vm.deliveryMode == DeliveryMode.HOME && vm.defaultAddress == null) {
                Text("Ajoutez une adresse pour la livraison à domicile.", color = Muted, fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            PrimaryButton("Valider la commande", onValidate, enabled = vm.canPlaceOrder)
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
