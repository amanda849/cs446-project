package cs486.splash.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthenticationException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause) {}

/**
 * A Singleton object that is responsible for interacting with the user account
 */
object UserRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseUser: FirebaseUser? = firebaseAuth.currentUser

    fun userSignIn(email: String, password: String): FirebaseUser?{
        var user: FirebaseUser? = null

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                user = it.result.user
            } else {
                throw AuthenticationException(it.exception.toString())
            }
        }

        return user
    }

    fun userSignUp(email: String, password: String): FirebaseUser?{
        var user: FirebaseUser? = null

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if(it.isSuccessful){
                user = it.result.user
            } else {
                throw AuthenticationException(it.exception.toString())
            }
        }

        return user
    }

    fun userSignOut(){
        firebaseAuth.signOut()
        firebaseUser = null
    }

    fun getUser(): FirebaseUser?{
        return firebaseUser
    }
}