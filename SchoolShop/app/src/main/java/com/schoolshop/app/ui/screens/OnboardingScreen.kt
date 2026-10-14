package com.schoolshop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.ui.components.PrimaryButton
import com.schoolshop.app.ui.theme.*

private data class Page(val emoji: String, val title: String, val text: String)

private val pages = listOf(
    Page("📚", "Des fournitures scolaires de qualité", "Cahiers, stylos, cartables, crayons… Tout ce dont vous avez besoin pour réussir votre année scolaire."),
    Page("🛒", "Commandez en quelques clics", "Parcourez les catégories, ajoutez au panier et validez votre commande facilement."),
    Page("🚚", "Livraison rapide", "Recevez vos fournitures à domicile ou retirez-les en magasin sous 24h."),
)

@Composable
fun OnboardingScreen(onStart: () -> Unit) {
    val state = rememberPagerState { pages.size }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(state, Modifier.weight(1f)) { i ->
            val p = pages[i]
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(p.emoji, fontSize = 110.sp)
                Spacer(Modifier.height(24.dp))
                Text(p.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Ink)
                Spacer(Modifier.height(12.dp))
                Text(p.text, textAlign = TextAlign.Center, color = Muted)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 16.dp)) {
            repeat(pages.size) { i ->
                Box(
                    Modifier.size(8.dp).clip(CircleShape)
                        .background(if (state.currentPage == i) Blue else Line),
                )
            }
        }
        PrimaryButton("Commencer", onStart)
    }
}
