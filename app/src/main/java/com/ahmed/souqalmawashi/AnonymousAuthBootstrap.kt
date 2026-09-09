package com.ahmed.souqalmawashi

import com.google.firebase.auth.FirebaseAuth

/**
 * يضمن وجود هوية Firebase (ولو مجهولة) لكل جهاز مثبّت عليه التطبيق.
 * يُستدعى مرة واحدة في MainActivity قبل عرض التطبيق.
 * لا يظهر أي شيء للمستخدم — يحدث بصمت في الخلفية.
 */
object AnonymousAuthBootstrap {

    fun ensureSignedIn(onReady: () -> Unit) {
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            onReady()
            return
        }

        auth.signInAnonymously()
            .addOnSuccessListener { onReady() }
            .addOnFailureListener {
                // حتى لو فشل (مثلاً بدون إنترنت)، نكمل عرض التطبيق
                onReady()
            }
    }
}
