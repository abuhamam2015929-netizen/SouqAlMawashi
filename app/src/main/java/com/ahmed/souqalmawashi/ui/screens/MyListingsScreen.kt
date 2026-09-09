package com.ahmed.souqalmawashi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ahmed.souqalmawashi.ui.ListingViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(viewModel: ListingViewModel) {
    val listings by viewModel.listings.collectAsState()

    // معرّف الجهاز الحالي (من Anonymous Auth) — نعرض فقط إعلانات هذا الجهاز
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val myListings = listings.filter { it.userId == currentUserId }

    Scaffold(topBar = { TopAppBar(title = { Text("إعلاناتي") }) }) { padding ->
        if (myListings.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد إعلانات بعد")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(myListings, key = { it.id }) { listing ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(listing.title)
                                Text("${listing.governorate} - ${listing.district}")
                            }
                            IconButton(onClick = { viewModel.deleteListing(listing.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف")
                            }
                        }
                    }
                }
            }
        }
    }
}
