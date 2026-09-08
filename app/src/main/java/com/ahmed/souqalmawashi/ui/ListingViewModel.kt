package com.ahmed.souqalmawashi.ui

import androidx.lifecycle.ViewModel
import com.ahmed.souqalmawashi.data.ListingRepository
import com.ahmed.souqalmawashi.data.MockListingRepository
import com.ahmed.souqalmawashi.model.Listing

/**
 * ViewModel وسيط بين الشاشات ومصدر البيانات.
 * لتبديل المصدر لاحقًا إلى Firestore: غيّر repository هنا فقط.
 */
class ListingViewModel(
    private val repository: ListingRepository = MockListingRepository
) : ViewModel() {

    val listings = repository.listings

    fun addListing(listing: Listing) = repository.addListing(listing)

    fun deleteListing(id: String) = repository.deleteListing(id)

    fun getListingById(id: String): Listing? = repository.getListingById(id)
}
