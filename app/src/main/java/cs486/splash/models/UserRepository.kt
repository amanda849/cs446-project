package cs486.splash.models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import cs486.splash.shared.UserProfile
import kotlinx.coroutines.tasks.await


class AuthenticationException(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause)

/**
 * A Singleton object that is responsible for interacting with the user account
 */
object UserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var user: FirebaseUser? = firebaseAuth.currentUser // should be deleted

    private const val TAG = "USER_REPO"
    init {
        firebaseAuth.addAuthStateListener {
            user = it.currentUser
        }
    }

    suspend fun userSignIn(email: String, password: String) {
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw AuthenticationException(e.message?: "Sign up failed. Please try again.")
        }
    }

    suspend fun userSignUp(email: String, password: String) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            throw AuthenticationException(e.message?: "Sign up failed. Please try again.")
        }
    }

    fun userSignOut() {
        firebaseAuth.signOut()
    }

    fun getUser(): FirebaseUser? {
        return user
    }

    fun updateUserProfile(updates: Map<String, Any>){
        fireStore.collection("User Profiles").document(user!!.uid)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(TAG, "setUserProfile: set user profile")
            }
            .addOnFailureListener{
                Log.e(TAG, "setUserProfile: ${it.message}")
            }
    }

    fun getUserProfile(): DocumentReference {
        return fireStore.collection("User Profiles").document(user!!.uid)
    }
}
