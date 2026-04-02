package com.example.eventhistory

import com.google.firebase.firestore.PropertyName
import java.util.Date

/**
 * Firestoreの構造に合わせたデータモデルです。
 */
data class EventEntry(
    val id: String = "", // FirestoreのドキュメントID
    val userId: String = "",
    val category: String = "",
    @get:PropertyName("item_name") @set:PropertyName("item_name") var itemName: String = "",
    @get:PropertyName("action_date") @set:PropertyName("action_date") var actionDate: Date = Date(),
    val memo: String = ""
)
