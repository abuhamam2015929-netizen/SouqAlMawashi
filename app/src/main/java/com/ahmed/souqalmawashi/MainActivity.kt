package com.ahmed.souqalmawashi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ahmed.souqalmawashi.navigation.Screen
import com.ahmed.souqalmawashi.ui.ListingViewModel
import com.ahmed.souqalmawashi.ui.screens.AddListingScreen
import com.ahmed.souqalmawashi.ui.screens.HomeScreen
import com.ahmed.souqalmawashi.ui.screens.ListingDetailScreen
import com.ahmed.souqalmawashi.ui.screens.MyListingsScreen
import com.ahmed.souqalmawashi.ui.theme.SouqAlMawashiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SouqAlMawashiTheme {
                var isAuthReady by remember { mutableStateOf(false) }

                // تسجيل دخول مجهول بصمت، بدون أي شاشة أو تدخل من المستخدم
                LaunchedEffect(Unit) {
                    AnonymousAuthBootstrap.ensureSignedIn { isAuthReady = true }
                }

                if (isAuthReady) {
                    AppRoot()
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<ListingViewModel>()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onListingClick = { id -> navController.navigate(Screen.Detail.createRoute(id)) }
                )
            }
            composable(Screen.AddListing.route) {
                AddListingScreen(
                    viewModel = viewModel,
                    onSaved = { navController.navigate(Screen.Home.route) }
                )
            }
            composable(Screen.MyListings.route) {
                MyListingsScreen(viewModel = viewModel)
            }
            composable(Screen.Detail.route) { backStackEntry ->
                val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
                ListingDetailScreen(
                    listingId = listingId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("الرئيسية") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.AddListing.route,
            onClick = { navController.navigate(Screen.AddListing.route) },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            label = { Text("نشر إعلان") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.MyListings.route,
            onClick = { navController.navigate(Screen.MyListings.route) },
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("إعلاناتي") }
        )
    }
}
