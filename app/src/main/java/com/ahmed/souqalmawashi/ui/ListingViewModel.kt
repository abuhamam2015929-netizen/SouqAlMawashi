package com.ahmed.souqalmawashi.ui

import androidx.lifecycle.ViewModel
import com.ahmed.souqalmawashi.data.FirestoreListingRepository
import com.ahmed.souqalmawashi.data.ListingRepository
import com.ahmed.souqalmawashi.model.Listing

class ListingViewModel(
    private val repository: ListingRepository = FirestoreListingRepository
) : ViewModel() {

    val listings = repository.listings

    fun addListing(listing: Listing) = repository.addListing(listing)

    fun deleteListing(id: String) = repository.deleteListing(id)

    fun getListingById(id: String): Listing? = repository.getListingById(id)
}
