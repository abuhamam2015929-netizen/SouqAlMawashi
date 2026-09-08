package com.ahmed.souqalmawashi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ahmed.souqalmawashi.model.AnimalType
import com.ahmed.souqalmawashi.model.Listing
import com.ahmed.souqalmawashi.ui.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ListingViewModel,
    onListingClick: (String) -> Unit
) {
    val listings by viewModel.listings.collectAsState()
    var selectedType by remember { mutableStateOf<AnimalType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = listings.filter { listing ->
        (selectedType == null || listing.animalType == selectedType) &&
            (searchQuery.isBlank() ||
                listing.title.contains(searchQuery, ignoreCase = true) ||
                listing.governorate.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("سوق المواشي اليمني") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                placeholder = { Text("ابحث عن حيوان أو محافظة...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            ScrollableTabRow(
                selectedTabIndex = if (selectedType == null) 0 else AnimalType.entries.indexOf(selectedType) + 1,
                edgePadding = 8.dp
            ) {
                Tab(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    text = { Text("الكل") }
                )
                AnimalType.entries.forEach { type ->
                    Tab(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        text = { Text(type.arabicLabel) }
                    )
                }
            }

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد إعلانات مطابقة حاليًا")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { listing ->
                        ListingCard(listing = listing, onClick = { onListingClick(listing.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun ListingCard(listing: Listing, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(10.dp)) {
            AsyncImage(
                model = listing.imageUrls.firstOrNull(),
                contentDescription = listing.title,
                modifier = Modifier.size(90.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(listing.animalType.arabicLabel, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${listing.governorate} - ${listing.district}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    if (listing.price > 0) "${listing.price} ريال" else "السعر: اتصل للاستفسار",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
