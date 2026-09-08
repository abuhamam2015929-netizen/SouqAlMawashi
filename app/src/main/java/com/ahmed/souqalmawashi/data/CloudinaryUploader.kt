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
import java.io.IOException

/**
 * مسؤول عن رفع صورة واحدة إلى Cloudinary باستخدام Unsigned Upload Preset
 * ويُرجع رابط secure_url عند النجاح، أو يرمي استثناء عند الفشل
 */
object CloudinaryUploader {

    // بيانات حساب Cloudinary الخاص بمشروع سوق المواشي اليمني
    private const val CLOUD_NAME = "dpnmoo9q"
    private const val UPLOAD_PRESET = "souq_mawashi_preset"
    private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"

    private val client = OkHttpClient()

    /**
     * @param context مطلوب لقراءة الصورة من الـ Uri عبر ContentResolver
     * @param uri رابط الصورة المختارة من المعرض
     * @return secure_url الخاص بالصورة على Cloudinary
     */
    suspend fun uploadImage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val imageFile = uriToTempFile(context, uri)
        try {
            uploadFile(imageFile)
        } finally {
            // تنظيف الملف المؤقت بعد الرفع (سواء نجح أو فشل)
            imageFile.delete()
        }
    }

    private fun uriToTempFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("تعذر فتح الصورة المختارة")

        val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()
        return tempFile
    }

    private fun uploadFile(imageFile: File): String {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .addFormDataPart(
                "file",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url(UPLOAD_URL)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            val bodyString = response.body?.string()
                ?: throw IOException("استجابة فارغة من Cloudinary")

            if (!response.isSuccessful) {
                throw IOException("فشل الرفع إلى Cloudinary: ${response.code} - $bodyString")
            }

            val json = JSONObject(bodyString)
            return json.getString("secure_url")
        }
    }
}
