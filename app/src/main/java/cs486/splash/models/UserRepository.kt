package cs486.splash.models

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import cs486.splash.shared.UserProfile


class AuthenticationException(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause)

/**
 * A Singleton object that is responsible for interacting with the user account
 */
object UserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var user: FirebaseUser? = firebaseAuth.currentUser // should be deleted

    init {
        firebaseAuth.addAuthStateListener {
            user = it.currentUser
        }
    }

    fun userSignIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) {
                throw AuthenticationException(it.exception.toString())
            }
        }
    }

    fun userSignUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) {
                throw AuthenticationException(it.exception.toString())
            }
        }
    }

    fun userSignOut() {
        firebaseAuth.signOut()
    }

    fun getUser(): FirebaseUser? {
        return user
    }

    fun setUserProfile(userProfile: UserProfile){
        fireStore.collection("User Profiles").document(user!!.uid)
            .set(userProfile.toHashMap(), SetOptions.merge())
            .addOnSuccessListener {
                Log.i("UserRepository", "setUserProfile: set user profile")
            }
            .addOnFailureListener{
                Log.e("UserRepository", "setUserProfile: ${it.message}")
            }
    }

    fun getUserProfile(): DocumentReference {
        return fireStore.collection("User Profiles").document(user!!.uid)
    }
}
