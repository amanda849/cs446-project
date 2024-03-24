package cs486.splash.models

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object ContentRepository {
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun fetchAllContent(): CollectionReference{
        return firebaseFirestore.collection("Contents")
    }
}