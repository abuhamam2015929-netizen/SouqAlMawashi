package com.ahmed.souqalmawashi.data

import com.ahmed.souqalmawashi.model.Listing
import com.ahmed.souqalmawashi.model.ListingStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FirestoreListingRepository : ListingRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("listings")

    private val _listings = MutableStateFlow<List<Listing>>(emptyList())
    override val listings: StateFlow<List<Listing>> = _listings.asStateFlow()

    init {
        collection
            .whereEqualTo("status", ListingStatus.ACTIVE.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                _listings.value = snapshot.documents.mapNotNull { it.toObject(Listing::class.java) }
            }
    }

    override fun addListing(listing: Listing, onResult: (Result<Unit>) -> Unit) {
        val docRef = if (listing.id.isBlank()) {
            collection.document()
        } else {
            collection.document(listing.id)
        }

        val listingWithId = listing.copy(id = docRef.id)

        docRef.set(listingWithId)
            .addOnSuccessListener {
                onResult(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                onResult(Result.failure(e))
            }
    }

    override fun deleteListing(id: String) {
        collection.document(id).delete()
    }

    override fun getListingById(id: String): Listing? =
        _listings.value.find { it.id == id }
}
