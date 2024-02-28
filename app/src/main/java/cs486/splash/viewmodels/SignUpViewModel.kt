package cs486.splash.viewmodels

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import cs486.splash.models.UserRepository

class SignUpViewModel {
    val email: MutableLiveData<String?> = MutableLiveData(null)
    val password: MutableLiveData<String?> = MutableLiveData(null)

    fun signUp(): FirebaseUser?{
        return UserRepository.userSignUp(email.value!!, password.value!!)
    }
}