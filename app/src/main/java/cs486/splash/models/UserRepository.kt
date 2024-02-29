package cs486.splash.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthenticationException(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause)

/**
 * A Singleton object that is responsible for interacting with the user account
 */
object UserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = firebaseAuth.currentUser
    init {
        firebaseAuth.addAuthStateListener {
            user = it.currentUser
        }
    }

    fun userSignIn(email: String, password: String): FirebaseUser? {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) {
                throw AuthenticationException(it.exception.toString())
            }
        }
        return user
    }

    fun userSignUp(email: String, password: String): FirebaseUser? {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) {
                throw AuthenticationException(it.exception.toString())
            }
        }

        return user
    }

    fun userSignOut() {
        firebaseAuth.signOut()
    }

    fun getUser(): FirebaseUser? {
        return user
    }
}
