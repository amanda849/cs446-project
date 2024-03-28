package cs486.splash.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import cs486.splash.models.AuthenticationException
import cs486.splash.models.Content
import cs486.splash.models.Content.Companion.toContent
import cs486.splash.models.ContentRepository
import cs486.splash.models.UserRepository
import com.google.firebase.firestore.EventListener
import cs486.splash.shared.UserProfile
import cs486.splash.shared.UserProfile.Companion.toProfile

class UserViewModel : ViewModel() {
    val TAG = "USER_VIEW_MODEL"

    private var _user: MutableLiveData<FirebaseUser?> = MutableLiveData(null)
    val user: LiveData<FirebaseUser?> get() = _user

    private var _userProfile: MutableLiveData<UserProfile?> = MutableLiveData(null)
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            _user.value = it.currentUser
            if(it.currentUser != null){
                getUserProfile()
            }
        }
    }

    suspend fun signIn(email: String, password: String){
        UserRepository.userSignIn(email, password)
    }

    suspend fun signUp(email: String, password: String, confirmPassword: String){
        if(confirmPassword != password){
            throw AuthenticationException("The passwords you entered do not match. Please make sure your passwords match exactly.")
        } else {
            UserRepository.userSignUp(email, password)
        }
    }

    fun setUserName(name: String){
        val updateMap = hashMapOf<String, Any>(
            "name" to name
        )

        UserRepository.updateUserProfile(updateMap)
    }

    fun setBirthDate(birthDate: String){
        val updateMap = hashMapOf<String, Any>(
            "birthDate" to birthDate
        )

        UserRepository.updateUserProfile(updateMap)
    }

    fun getUserProfile() {
        UserRepository.getUserProfile()?.get()
            ?.addOnSuccessListener { snapshot ->
                Log.e(TAG, snapshot.toString())
                _userProfile.value = snapshot.toProfile()
            }
            ?.addOnFailureListener { exception ->
                Log.w(TAG, "Get failed.", exception)
            }
    }

    suspend fun resetPassword(email: String){
        UserRepository.resetPassword(email)
    }

    suspend fun updatePassword(newPass: String, confirmPass: String){
        if(confirmPass != newPass){
            throw AuthenticationException("The passwords you entered do not match. Please make sure your passwords match exactly.")
        } else {
            UserRepository.updatePassword(newPass)
        }
    }

    suspend fun updateEmail(newEmail: String, confirmEmail: String){
        if(newEmail != confirmEmail){
            throw AuthenticationException("The emails you entered do not match. Please make sure your passwords match exactly.")
        } else {
            UserRepository.updateEmail(newEmail)
        }
    }
}