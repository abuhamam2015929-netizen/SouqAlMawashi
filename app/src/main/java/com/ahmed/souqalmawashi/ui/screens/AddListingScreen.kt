package com.ahmed.souqalmawashi.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ahmed.souqalmawashi.data.CloudinaryUploader
import com.ahmed.souqalmawashi.model.AnimalType
import com.ahmed.souqalmawashi.model.Listing
import com.ahmed.souqalmawashi.ui.ListingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    viewModel: ListingViewModel,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var animalType by remember { mutableStateOf(AnimalType.SHEEP) }
    var animalTypeExpanded by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") } // نص فارغ = غير محدد
    var governorate by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "نشر إعلان جديد", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // نوع الحيوان (قائمة منسدلة)
        ExposedDropdownMenuBox(
            expanded = animalTypeExpanded,
            onExpandedChange = { animalTypeExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = animalType.arabicLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("نوع الحيوان") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = animalTypeExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = animalTypeExpanded,
                onDismissRequest = { animalTypeExpanded = false }
            ) {
                AnimalType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.arabicLabel) },
                        onClick = {
                            animalType = type
                            animalTypeExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("عنوان الإعلان") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("الوصف") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it.filter { c -> c.isDigit() } },
            label = { Text("السعر (اختياري)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("المديرية") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = governorate,
                onValueChange = { governorate = it },
                label = { Text("المحافظة") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contactPhone,
            onValueChange = { contactPhone = it },
            label = { Text("رقم واتساب للتواصل") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // معاينة الصورة المختارة
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .padding(8.dp)
            )
        }

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text(if (imageUri == null) "اختر صورة الحيوان" else "تغيير الصورة")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (title.isBlank() || governorate.isBlank() || district.isBlank() || contactPhone.isBlank()) {
                    errorMessage = "الرجاء تعبئة عنوان الإعلان والمحافظة والمديرية ورقم واتساب"
                    return@Button
                }

                isLoading = true
                errorMessage = null

                scope.launch {
                    try {
                        // 1. رفع الصورة إلى Cloudinary إن وُجدت
                        val imageUrls: List<String> = if (imageUri != null) {
                            listOf(CloudinaryUploader.uploadImage(context, imageUri!!))
                        } else {
                            emptyList()
                        }

                        // 2. بناء كائن الإعلان وحفظه عبر الـ ViewModel
                        val listing = Listing(
                            animalType = animalType,
                            title = title,
                            description = description,
                            price = price.toLongOrNull() ?: 0L,
                            imageUrls = imageUrls,
                            governorate = governorate,
                            district = district,
                            contactPhone = contactPhone
                        )
                        viewModel.addListing(listing)

                        isLoading = false
                        onSaved()
                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = "خطأ في نشر الإعلان: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("نشر الإعلان")
            }
        }
    }
}
