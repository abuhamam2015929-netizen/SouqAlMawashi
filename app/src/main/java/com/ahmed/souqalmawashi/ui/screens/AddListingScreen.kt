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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AddListingScreen(onListingAdded: () -> Unit) {
    val context = LocalContext.value
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("إبل") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // منتقي الصور من المعرض
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
        }
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

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("عنوان الإعلان (مثلاً: جمل محلي)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("السعر (ريال)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("الموقع (مثلاً: شبوة - بيحان)") },
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
            Text("اختر صورة الحيوان")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (title.isBlank() || price.isBlank() || imageUri == null) {
                    errorMessage = "الرجاء تعبئة الحقول المطلوبة واختيار صورة"
                    return@Button
                }

                isLoading = true
                errorMessage = null

                scope.launch {
                    try {
                        // 1. رفع الصورة إلى Cloudinary والحصول على الرابط الآمن
                        val uploadedImageUrl = CloudinaryUploader.uploadImage(context, imageUri!!)

                        // 2. تجهيز بيانات الإعلان لحفظها في Firestore
                        val listingId = UUID.randomUUID().toString()
                        val listingMap = hashMapOf(
                            "id" to listingId,
                            "title" to title,
                            "price" to price.toDoubleOrNull(),
                            "location" to location,
                            "category" to category,
                            "imageUrl" to uploadedImageUrl,
                            "timestamp" to System.currentTimeMillis()
                        )

                        // 3. حفظ المستند في مجموعة "listings"
                        FirebaseFirestore.getInstance()
                            .collection("listings")
                            .document(listingId)
                            .set(listingMap)
                            .addOnSuccessListener {
                                isLoading = false
                                onListingAdded() // العودة للرئيسية أو تحديث القائمة
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                errorMessage = "فشل حفظ الإعلان: ${e.localizedMessage}"
                            }

                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = "خطأ في الرفع: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("نشر الإعلان")
            }
        }
    }
}
