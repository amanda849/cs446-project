package cs486.splash.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import cs486.splash.models.UserRepository

class SignInViewModel : ViewModel() {
    val email: MutableLiveData<String?> = MutableLiveData(null)
    val password: MutableLiveData<String?> = MutableLiveData(null)

    fun signIn(): FirebaseUser?{
        return UserRepository.userSignIn(email.value!!, password.value!!)
    }
}