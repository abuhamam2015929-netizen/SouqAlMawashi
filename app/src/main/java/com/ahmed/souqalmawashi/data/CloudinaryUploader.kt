package com.ahmed.souqalmawashi.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object CloudinaryUploader {

    private const val CLOUD_NAME = "ضع_اسم_الحساب_هنا"
    private const val UPLOAD_PRESET = "ضع_اسم_البريست_هنا"

    private val client = OkHttpClient()

    suspend fun uploadImage(context: Context, imageUri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalStateException("تعذّر فتح الصورة المختارة")

        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { output -> inputStream.copyTo(output) }
        inputStream.close()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .addFormDataPart(
                "file",
                tempFile.name,
                tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            tempFile.delete()
            if (!response.isSuccessful) {
                throw IllegalStateException("فشل رفع الصورة (${response.code})")
            }
            val body = response.body?.string()
                ?: throw IllegalStateException("استجابة فارغة من الخادم")
            JSONObject(body).getString("secure_url")
        }
    }
}
