package com.ahmed.souqalmawashi.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ahmed.souqalmawashi.ui.ListingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingId: String,
    viewModel: ListingViewModel,
    onBack: () -> Unit
) {
    val listing = viewModel.getListingById(listingId)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(listing?.title ?: "تفاصيل الإعلان") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        if (listing == null) {
            Box(Modifier.padding(padding).fillMaxSize()) { Text("الإعلان غير موجود") }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            AsyncImage(
                model = listing.imageUrls.firstOrNull(),
                contentDescription = listing.title,
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(listing.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("النوع: ${listing.animalType.arabicLabel}")
            Text("الموقع: ${listing.governorate} - ${listing.district}")
            Text(if (listing.price > 0) "السعر: ${listing.price} ريال" else "السعر: اتصل للاستفسار")
            Spacer(Modifier.height(12.dp))
            Text(listing.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val phone = listing.contactPhone.filter { it.isDigit() }
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://wa.me/$phone")
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("تواصل عبر واتساب")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { /* TODO: نموذج إبلاغ بسيط في الإصدار القادم */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("الإبلاغ عن هذا الإعلان")
            }
        }
    }
}
