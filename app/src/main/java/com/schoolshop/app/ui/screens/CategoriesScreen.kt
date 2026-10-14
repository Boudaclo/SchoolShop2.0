package com.schoolshop.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.schoolshop.app.ui.ShopViewModel
import com.schoolshop.app.ui.components.EmojiTile
import com.schoolshop.app.ui.components.ScreenTopBar
import com.schoolshop.app.ui.theme.*

@Composable
fun CategoriesScreen(vm: ShopViewModel, onCategory: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenTopBar("Catégories")
        LazyColumn {
            items(vm.categories) { c ->
                Row(
                    Modifier.fillMaxWidth().clickable { onCategory(c.id) }.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EmojiTile(c.emoji, 52.dp)
                    Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                        Text(c.name, fontWeight = FontWeight.SemiBold, color = Ink)
                        Text(c.subtitle, fontSize = 12.sp, color = Muted)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Muted)
                }
                HorizontalDivider(color = Line, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
