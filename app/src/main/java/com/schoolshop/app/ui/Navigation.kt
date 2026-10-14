package com.schoolshop.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.schoolshop.app.ui.screens.*

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CATEGORIES = "categories"
    const val CART = "cart"
    const val ACCOUNT = "account"
    const val PRODUCTS = "products/{categoryId}"
    const val DETAIL = "detail/{productId}"
    const val CHECKOUT = "checkout"
    const val CONFIRMATION = "confirmation"
    const val ORDERS = "orders"
    const val ADDRESSES = "addresses"
    const val PAYMENTS = "payments"
    const val FAVORITES = "favorites"
    const val SUPPORT = "support"
    fun products(categoryId: String) = "products/$categoryId"
    fun detail(productId: Int) = "detail/$productId"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.HOME, "Accueil", Icons.Default.Home),
    Tab(Routes.CATEGORIES, "Catégories", Icons.Default.Apps),
    Tab(Routes.CART, "Panier", Icons.Default.ShoppingCart),
    Tab(Routes.ACCOUNT, "Compte", Icons.Default.Person),
)

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val vm: ShopViewModel = viewModel()
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    val showBar = route in tabs.map { it.route }

    Scaffold(
        containerColor = Color.White,
        bottomBar = { if (showBar) BottomBar(nav, route, vm.cartCount) },
    ) { pad ->
        NavHost(nav, startDestination = Routes.SPLASH, modifier = Modifier.padding(pad)) {
            composable(Routes.SPLASH) {
                SplashScreen {
                    nav.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } }
                }
            }
            composable(Routes.ONBOARDING) {
                OnboardingScreen {
                    nav.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                }
            }
            composable(Routes.HOME) {
                HomeScreen(
                    vm,
                    onCategory = { nav.navigate(Routes.products(it)) },
                    onProduct = { nav.navigate(Routes.detail(it)) },
                    onCart = { goTab(nav, Routes.CART) },
                )
            }
            composable(Routes.CATEGORIES) {
                CategoriesScreen(vm, onCategory = { nav.navigate(Routes.products(it)) })
            }
            composable(
                Routes.PRODUCTS,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
            ) { entry ->
                ProductListScreen(
                    vm,
                    categoryId = entry.arguments?.getString("categoryId") ?: "cahiers",
                    onBack = { nav.popBackStack() },
                    onProduct = { nav.navigate(Routes.detail(it)) },
                )
            }
            composable(
                Routes.DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.IntType }),
            ) { entry ->
                ProductDetailScreen(
                    vm,
                    productId = entry.arguments?.getInt("productId") ?: 1,
                    onBack = { nav.popBackStack() },
                )
            }
            composable(Routes.CART) {
                CartScreen(vm, onCheckout = { nav.navigate(Routes.CHECKOUT) })
            }
            composable(Routes.CHECKOUT) {
                CheckoutScreen(
                    vm,
                    onBack = { nav.popBackStack() },
                    onManageAddresses = { nav.navigate(Routes.ADDRESSES) },
                    onManagePayments = { nav.navigate(Routes.PAYMENTS) },
                    onValidate = {
                        vm.placeOrder()
                        nav.navigate(Routes.CONFIRMATION) { popUpTo(Routes.HOME) }
                    },
                )
            }
            composable(Routes.CONFIRMATION) {
                ConfirmationScreen(
                    vm,
                    onOrders = { nav.navigate(Routes.ORDERS) },
                    onHome = { goTab(nav, Routes.HOME) },
                )
            }
            composable(Routes.ACCOUNT) {
                AccountScreen(
                    vm,
                    onOrders = { nav.navigate(Routes.ORDERS) },
                    onAddresses = { nav.navigate(Routes.ADDRESSES) },
                    onPayments = { nav.navigate(Routes.PAYMENTS) },
                    onFavorites = { nav.navigate(Routes.FAVORITES) },
                    onSupport = { nav.navigate(Routes.SUPPORT) },
                )
            }
            composable(Routes.ADDRESSES) { AddressesScreen(vm, onBack = { nav.popBackStack() }) }
            composable(Routes.PAYMENTS) { PaymentsScreen(vm, onBack = { nav.popBackStack() }) }
            composable(Routes.FAVORITES) {
                FavoritesScreen(vm, onBack = { nav.popBackStack() }, onProduct = { nav.navigate(Routes.detail(it)) })
            }
            composable(Routes.SUPPORT) { SupportScreen(onBack = { nav.popBackStack() }) }
            composable(Routes.ORDERS) { OrdersScreen(vm, onBack = { nav.popBackStack() }) }
        }
    }
}

private fun goTab(nav: NavController, route: String) {
    nav.navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun BottomBar(nav: NavController, current: String?, cartCount: Int) {
    NavigationBar(containerColor = Color.White) {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = current == tab.route,
                onClick = { goTab(nav, tab.route) },
                icon = {
                    BadgedBox(badge = { if (tab.route == Routes.CART && cartCount > 0) Badge { Text("$cartCount") } }) {
                        Icon(tab.icon, tab.label)
                    }
                },
                label = { Text(tab.label) },
            )
        }
    }
}
