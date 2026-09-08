package com.ahmed.souqalmawashi.data

import com.ahmed.souqalmawashi.model.AnimalType
import com.ahmed.souqalmawashi.model.Listing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * واجهة مصدر بيانات الإعلانات.
 *
 * حاليًا: [MockListingRepository] تنفيذ وهمي بالذاكرة فقط، للتطوير والتجربة
 * بدون الحاجة لإعداد Firebase الآن.
 *
 * الخطوة القادمة (عندما نجهز Firebase Firestore + Storage خطوة بخطوة):
 * ننشئ FirestoreListingRepository يطبّق نفس الواجهة بالضبط،
 * ونستبدلها في MainActivity بسطر واحد فقط دون تعديل أي شاشة.
 */
interface ListingRepository {
    val listings: StateFlow<List<Listing>>
    fun addListing(listing: Listing)
    fun deleteListing(id: String)
    fun getListingById(id: String): Listing?
}

object MockListingRepository : ListingRepository {

    private val sampleData = listOf(
        Listing(
            id = "1",
            animalType = AnimalType.SHEEP,
            title = "خروف نعيمي ممتاز",
            description = "خروف نعيمي عمر سنة، تسمين ممتاز، جاهز للذبح أو التربية.",
            price = 85000,
            imageUrls = listOf("https://picsum.photos/seed/sheep1/600/400"),
            governorate = "صنعاء",
            district = "بني حشيش",
            contactPhone = "9677xxxxxxx"
        ),
        Listing(
            id = "2",
            animalType = AnimalType.COW,
            title = "بقرة حلوب",
            description = "بقرة حلوب إنتاج جيد، عمر ٣ سنوات، فحص بيطري حديث.",
            price = 450000,
            imageUrls = listOf("https://picsum.photos/seed/cow1/600/400"),
            governorate = "إب",
            district = "المدينة",
            contactPhone = "9677xxxxxxx"
        ),
        Listing(
            id = "3",
            animalType = AnimalType.CAMEL,
            title = "جمل هجين للبيع",
            description = "جمل هجين، صحة ممتازة، مناسب للسباق أو التربية.",
            price = 1200000,
            imageUrls = listOf("https://picsum.photos/seed/camel1/600/400"),
            governorate = "مأرب",
            district = "مأرب المدينة",
            contactPhone = "9677xxxxxxx"
        ),
        Listing(
            id = "4",
            animalType = AnimalType.POULTRY,
            title = "دجاج بلدي",
            description = "دجاج بلدي بياض، دفعة ٢٠ رأس.",
            price = 6000,
            imageUrls = listOf("https://picsum.photos/seed/poultry1/600/400"),
            governorate = "تعز",
            district = "المظفر",
            contactPhone = "9677xxxxxxx"
        )
    )

    private val _listings = MutableStateFlow(sampleData)
    override val listings: StateFlow<List<Listing>> = _listings.asStateFlow()

    override fun addListing(listing: Listing) {
        _listings.value = listOf(listing) + _listings.value
    }

    override fun deleteListing(id: String) {
        _listings.value = _listings.value.filterNot { it.id == id }
    }

    override fun getListingById(id: String): Listing? =
        _listings.value.find { it.id == id }
}
