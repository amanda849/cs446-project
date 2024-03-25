package cs486.splash.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import cs486.splash.models.UserRepository
import cs486.splash.shared.UserProfile

class AuthViewModel : ViewModel() {
    val TAG = "AUTH_VIEW_MODEL"

    private var _user: MutableLiveData<FirebaseUser?> = MutableLiveData(null)
    val user: LiveData<FirebaseUser?> get() = _user

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            _user.value = it.currentUser
        }
    }

    fun signIn(email: String, password: String){
        UserRepository.userSignIn(email, password)
    }

    fun signUp(email: String, password: String, confirmPassword: String){
        // TODO: confirm that password and confirmPassword matches
        UserRepository.userSignUp(email, password)
    }

    fun setUserProfile(
        name: String = "",
        birthDate: String = "",
        familyDoctorName: String = "",
        familyDoctorPhone: String = "",
        familyDoctorEmail: String = ""
    ){
        val userProfile = UserProfile(
            name,
            birthDate,
            familyDoctorName,
            familyDoctorPhone,
            familyDoctorEmail
        )

        Log.e(TAG, "$name, $birthDate, $familyDoctorName")
        UserRepository.setUserProfile(userProfile)
    }
}