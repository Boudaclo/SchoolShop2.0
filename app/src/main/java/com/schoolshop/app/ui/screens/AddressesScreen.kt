package com.schoolshop.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.data.local.AddressEntity
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.PrimaryButton
import com.schoolshop.app.ui.components.ScreenTopBar
import com.schoolshop.app.ui.theme.*

@Composable
fun AddressesScreen(vm: ShopViewModel, onBack: () -> Unit) {
    var dialogOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AddressEntity?>(null) }
    var toDelete by remember { mutableStateOf<AddressEntity?>(null) }

    if (dialogOpen) {
        AddressDialog(
            initial = editing,
            onDismiss = { dialogOpen = false },
            onSave = { label, details, makeDefault ->
                vm.saveAddress(
                    AddressEntity(
                        id = editing?.id ?: 0,
                        label = label,
                        details = details,
                        isDefault = makeDefault || editing?.isDefault == true,
                    )
                )
                dialogOpen = false
            },
        )
    }
    toDelete?.let { a ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Supprimer l'adresse ?") },
            text = { Text(a.label) },
            confirmButton = { TextButton({ vm.deleteAddress(a.id); toDelete = null }) { Text("Supprimer") } },
            dismissButton = { TextButton({ toDelete = null }) { Text("Annuler") } },
        )
    }

    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Mes adresses", onBack)
        if (vm.addresses.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), Alignment.Center) {
                Text("Aucune adresse enregistrée", color = Muted)
            }
        } else {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(vm.addresses, key = { it.id }) { a ->
                    Card(colors = CardDefaults.cardColors(containerColor = BlueLight)) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(a.label, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                if (a.isDefault) Text("Par défaut", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(a.details, color = Muted, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!a.isDefault) TextButton({ vm.setDefaultAddress(a.id) }) { Text("Définir par défaut") }
                                Spacer(Modifier.weight(1f))
                                IconButton({ editing = a; dialogOpen = true }) { Icon(Icons.Default.Edit, "Modifier") }
                                IconButton({ toDelete = a }) { Icon(Icons.Default.Delete, "Supprimer") }
                            }
                        }
                    }
                }
            }
        }
        PrimaryButton("Ajouter une adresse", { editing = null; dialogOpen = true }, Modifier.padding(16.dp))
    }
}

@Composable
private fun AddressDialog(initial: AddressEntity?, onDismiss: () -> Unit, onSave: (String, String, Boolean) -> Unit) {
    var label by remember { mutableStateOf(initial?.label ?: "Domicile") }
    var details by remember { mutableStateOf(initial?.details ?: "") }
    var makeDefault by remember { mutableStateOf(initial?.isDefault ?: false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nouvelle adresse" else "Modifier l'adresse") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(label, { label = it }, label = { Text("Nom (Domicile, Bureau…)") }, singleLine = true)
                OutlinedTextField(details, { details = it }, label = { Text("Adresse") }, minLines = 2)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(makeDefault, { makeDefault = it }, enabled = initial?.isDefault != true)
                    Text("Adresse par défaut")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(label.trim(), details.trim(), makeDefault) },
                enabled = label.isNotBlank() && details.isNotBlank(),
            ) { Text("Enregistrer") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Annuler") } },
    )
}
