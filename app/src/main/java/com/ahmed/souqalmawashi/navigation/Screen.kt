package com.ahmed.souqalmawashi.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddListing : Screen("add_listing")
    data object MyListings : Screen("my_listings")
    data object Detail : Screen("detail/{listingId}") {
        fun createRoute(listingId: String) = "detail/$listingId"
    }
}
