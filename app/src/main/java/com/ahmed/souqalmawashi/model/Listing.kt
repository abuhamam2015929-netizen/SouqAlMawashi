package com.ahmed.souqalmawashi.model

/**
 * نموذج إعلان في سوق المواشي.
 * نفس الحقول ستُستخدم لاحقًا كما هي عند الانتقال إلى Firestore.
 */
data class Listing(
    val id: String = "",
    val userId: String = "guest",
    val animalType: AnimalType = AnimalType.SHEEP,
    val title: String = "",
    val description: String = "",
    val price: Long = 0L, // 0 = السعر غير محدد / اتصل للسعر
    val imageUrls: List<String> = emptyList(),
    val governorate: String = "",
    val district: String = "",
    val contactPhone: String = "",
    val status: ListingStatus = ListingStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AnimalType(val arabicLabel: String) {
    SHEEP("غنم"),
    COW("بقر"),
    CAMEL("إبل"),
    POULTRY("دواجن")
}

enum class ListingStatus {
    ACTIVE, SOLD, EXPIRED
}
