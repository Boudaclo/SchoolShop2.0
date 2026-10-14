package com.schoolshop.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.ui.components.OutlineButton
import com.schoolshop.app.ui.components.ScreenTopBar
import com.schoolshop.app.ui.theme.*

/** À MODIFIER : coordonnées réelles du support SchoolShop. */
object SupportConfig {
    const val PHONE = "+22600000000"
    const val WHATSAPP = "22600000000" // format international, sans le +
    const val EMAIL = "support@schoolshop.example"
}

private val faq = listOf(
    "Quels sont les délais de livraison ?" to
        "Livraison à domicile sous 1 à 3 jours (500 FCFA). Retrait en magasin sous 24h, gratuit.",
    "Comment payer ma commande ?" to
        "Par Mobile Money (Orange Money, Moov Money) ou par carte bancaire. Vous pouvez enregistrer vos moyens de paiement dans Mon compte.",
    "Comment retrouver mes commandes ?" to
        "Dans Mon compte, ouvrez « Mes commandes » : vous y voyez le code, les articles et le total de chaque commande.",
    "Comment changer mon adresse de livraison ?" to
        "Dans Mon compte, ouvrez « Mes adresses » pour ajouter, modifier ou choisir l'adresse par défaut.",
)

@Composable
fun SupportScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var open by remember { mutableStateOf<Int?>(null) }
    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Aide & support", onBack)
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Text("Questions fréquentes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            faq.forEachIndexed { i, (q, a) ->
                Column(Modifier.fillMaxWidth().clickable { open = if (open == i) null else i }.padding(vertical = 10.dp)) {
                    Text(q, fontWeight = FontWeight.SemiBold)
                    if (open == i) Text(a, color = Muted, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
                HorizontalDivider(color = Line)
            }
            Spacer(Modifier.height(24.dp))
            Text("Nous contacter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            OutlineButton("Appeler", { launch(ctx, Intent(Intent.ACTION_DIAL, Uri.parse("tel:${SupportConfig.PHONE}"))) })
            Spacer(Modifier.height(8.dp))
            OutlineButton("WhatsApp", { launch(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${SupportConfig.WHATSAPP}"))) })
            Spacer(Modifier.height(8.dp))
            OutlineButton("E-mail", { launch(ctx, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${SupportConfig.EMAIL}"))) })
        }
    }
}

private fun launch(ctx: Context, intent: Intent) {
    try {
        ctx.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(ctx, "Aucune application disponible", Toast.LENGTH_SHORT).show()
    }
}
