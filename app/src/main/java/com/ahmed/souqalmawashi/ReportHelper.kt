package com.ahmed.souqalmawashi.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.ahmed.souqalmawashi.model.Listing

/**
 * يفتح واتساب مع رسالة جاهزة تحتوي تفاصيل الإعلان، لإرسالها لرقمك مباشرة كإبلاغ.
 */
object ReportHelper {

    private const val ADMIN_WHATSAPP_NUMBER = "967778488899"

    fun reportListing(context: Context, listing: Listing) {
        val message = buildString {
            append("🚩 إبلاغ عن إعلان في سوق المواشي اليمني\n\n")
            append("العنوان: ${listing.title}\n")
            append("النوع: ${listing.animalType.arabicLabel}\n")
            append("المحافظة: ${listing.governorate} - ${listing.district}\n")
            append("رقم الإعلان: ${listing.id}\n")
            append("رقم صاحب الإعلان: ${listing.contactPhone}\n\n")
            append("السبب: ")
        }

        val encodedMessage = Uri.encode(message)
        val uri = Uri.parse("https://wa.me/$ADMIN_WHATSAPP_NUMBER?text=$encodedMessage")

        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}
