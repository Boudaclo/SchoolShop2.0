package com.schoolshop.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.schoolshop.app.data.Product
import com.schoolshop.app.data.formatFcfa
import com.schoolshop.app.ui.theme.*

@Composable
fun EmojiTile(emoji: String, size: Dp, modifier: Modifier = Modifier, bg: Color = BlueLight) {
    Box(
        modifier.size(size).clip(RoundedCornerShape(14.dp)).background(bg),
        contentAlignment = Alignment.Center,
    ) { Text(emoji, fontSize = (size.value * 0.5f).sp) }
}

/** Photo du produit (Coil) ; affiche l'emoji tant que l'image charge ou si elle est absente. */
@Composable
fun ProductImage(product: Product, size: Dp, modifier: Modifier = Modifier) {
    val fallback: @Composable () -> Unit = {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(product.emoji, fontSize = (size.value * 0.5f).sp)
        }
    }
    Box(modifier.size(size).clip(RoundedCornerShape(14.dp)).background(BlueLight)) {
        if (product.imageUrl == null) fallback() else SubcomposeAsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
            loading = { fallback() },
            error = { fallback() },
        )
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
    ) { Text(text, fontWeight = FontWeight.SemiBold) }
}

@Composable
fun OutlineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Blue),
    ) { Text(text, fontWeight = FontWeight.SemiBold, color = Blue) }
}

@Composable
fun QuantityStepper(qty: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { if (qty > 1) onChange(qty - 1) }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Remove, "Moins")
        }
        Text("$qty", Modifier.padding(horizontal = 10.dp), fontWeight = FontWeight.SemiBold)
        IconButton(onClick = { onChange(qty + 1) }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Add, "Plus")
        }
    }
}

@Composable
fun ScreenTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour") }
        } else Spacer(Modifier.width(12.dp))
        Text(title, Modifier.weight(1f), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Ink)
        actions()
    }
}

@Composable
fun ProductRow(product: Product, onClick: () -> Unit, onAdd: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProductImage(product, 72.dp)
        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
            Text(product.name, fontWeight = FontWeight.SemiBold, color = Ink)
            Text(product.format, fontSize = 12.sp, color = Muted)
            Spacer(Modifier.height(6.dp))
            Text(formatFcfa(product.price), fontWeight = FontWeight.Bold, color = Blue)
        }
        FilledIconButton(onClick = onAdd, shape = RoundedCornerShape(10.dp), modifier = Modifier.size(38.dp)) {
            Icon(Icons.Default.ShoppingCart, "Ajouter au panier", Modifier.size(20.dp))
        }
    }
    HorizontalDivider(color = Line, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit, onAdd: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick)
            .background(Color.White).padding(10.dp),
    ) {
        ProductImage(product, 96.dp, Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Text(product.name, fontSize = 13.sp, color = Ink, maxLines = 1)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(formatFcfa(product.price), Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Blue, fontSize = 14.sp)
            Box(
                Modifier.size(30.dp).clip(CircleShape).background(Blue).clickable(onClick = onAdd),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Default.ShoppingCart, "Ajouter", tint = Color.White, modifier = Modifier.size(16.dp)) }
        }
    }
}
