package com.ahmed.souqalmawashi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ahmed.souqalmawashi.model.AnimalType
import com.ahmed.souqalmawashi.model.Listing
import com.ahmed.souqalmawashi.ui.ListingViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    viewModel: ListingViewModel,
    onSaved: () -> Unit
) {
    var animalType by remember { mutableStateOf(AnimalType.SHEEP) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var governorate by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("نشر إعلان جديد") }) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = animalType.arabicLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("نوع الحيوان") },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    AnimalType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.arabicLabel) },
                            onClick = { animalType = type; expanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("عنوان الإعلان") }, modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("الوصف") }, modifier = Modifier.fillMaxWidth(), minLines = 3
            )

            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = price, onValueChange = { price = it.filter { c -> c.isDigit() } },
                label = { Text("السعر (اختياري)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))
            Row {
                OutlinedTextField(
                    value = governorate, onValueChange = { governorate = it },
                    label = { Text("المحافظة") }, modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = district, onValueChange = { district = it },
                    label = { Text("المديرية") }, modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text("رقم واتساب للتواصل") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Text(
                "ملاحظة: رفع الصور سيُفعَّل عند إعداد Firebase Storage في الخطوة القادمة.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.addListing(
                        Listing(
                            id = UUID.randomUUID().toString(),
                            animalType = animalType,
                            title = title,
                            description = description,
                            price = price.toLongOrNull() ?: 0L,
                            governorate = governorate,
                            district = district,
                            contactPhone = phone,
                            imageUrls = listOf("https://picsum.photos/seed/${UUID.randomUUID()}/600/400")
                        )
                    )
                    onSaved()
                },
                enabled = title.isNotBlank() && phone.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("نشر الإعلان")
            }
        }
    }
}
